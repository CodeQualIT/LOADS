# LOADS

## Lightweight Object and Array Data Structure

This is a data structure library that provides a lightweight way to transfer data between different
applications.
Its design is inspired by the [JSON](https://www.json.org/json-en.html) format
and Stenway's [RSV](https://github.com/Stenway/RSV-Specification) format.

## Reasoning

Millions of applications are built every day, and they all need to transfer data between each other.
The most common way to do this is by using JSON. JSON is a great format, but it has some limitations
and drawbacks.
For example, all values are represented in human-readable text, which makes it inefficient for
transferring large amounts of data.
Also, JSON does not support binary data, which is a common requirement in many applications.

## Solution

> ### RSV's Special Bytes
> Instead of using length values prefixed, RSV uses special bytes to indicate the end of a value (
> EOV), or the end of a row (EOR).
>
> These terminating bytes are possible, because RSV strings are Unicode strings, and are encoded
> using UTF-8.
> With the UTF-8 encoding and it's specific byte pattern, there is a range of bytes, that will never
> be produced by the encoding scheme.
>
> | From     | To        | Byte 1    | Byte 2    | Byte 3    | Byte 4   |
> |----------|-----------|-----------|-----------|-----------|----------|
> | U+0000   | U+007F    | 0xxxxxxx  | -         | -         | -        |
> | U+0080   | U+07FF    | 110xxxxx  | 10xxxxxx  | -         | -        |
> | U+0800   | U+FFFF    | 1110xxxx  | 10xxxxxx  | 10xxxxxx  | -        |
> | U+10000  | U+10FFFF  | 11110xxx  | 10xxxxxx  | 10xxxxxx  | 10xxxxxx |
> 
> With the UTF-8 encoding and it's specific byte pattern, there is a range of bytes, that will never
> be produced by the encoding scheme.
>
> | Binary    | Hex  | Decimal |
> |-----------|------|---------|
> | 11111111  | FF   | 255     |
> | 11111110  | FE   | 254     |
> | 11111101  | FD   | 253     |
> | 11111100  | FC   | 252     |
> | 11111011  | FB   | 251     |
> | 11111010  | FA   | 250     |
> | 11111001  | F9   | 249     |
> | 11111000  | F8   | 248     |

Like RSV, LOADS stores its data largely in UTF-8 encoded binary format, with a couple of special
characters.

Where RSV only needs 3 of these 8 special bytes (end of value, end of row and null value), LOADS
will need 6 of them for the following:

- Element Separator    
- Start of Object      
- Start of Array       
- End of Object/Array  
- Start of binary value
- Null value

| Hex | Dec | Type                  | Description                                                                   | Mnemonic                                 |
|-----|-----|-----------------------|-------------------------------------------------------------------------------|------------------------------------------|
| FA  | 250 | Start of Array        |                                                                               | A for Array                              |
| FB  | 251 | Start of binary value |                                                                               | B for Binary                             |
| FC  | 252 | Start of Object       |                                                                               | C for Class, which is related to objects |
| FD  | 253 | Null value            | similar to RSV's null value                                                   | D for Dud, eg. no value / null           |
| FE  | 254 | End of Object/Array   | since LOADS is hierarchical, this can be the same for both objects and arrays | E for End                                |
| FF  | 255 | Element Separator     | similar to RSV's End of Value                                                 | F for Forward, eg. next element          |

## Syntax

### Strings
All strings are UTF-8 encoded. They can be used for values as well as object keys.
Strings are the most common data type in LOADS, so they don't need any special characters as prefix.

Example:
```
Hello 🌎!
```
Is encoded as:
```
72 101 108 108 111 32 240 159 140 142 33
```

### Arrays
Arrays are prefixed with the special byte `FA (250)` and contain a list of values separated by the special
byte `FF (255)` and is terminated by the special byte `FE (254)`.

Example:
```json
["Hello", "🌎"]
```
Is encoded as:
```
250 | 72 101 108 108 111 | 255 | 240 159 140 142 | 254 
```

### Objects
Objects are prefixed with the special byte `FC (252)` and contain a list of key-value pairs. 
Both key-value pairs and the keys and values themselves are separated by the special byte `FF (255)`.

Example:
```json
{"firstname": "John", "lastname": "Doe"}
```
Is encoded as:
```
252 | 102 105 114 115 116 110 97 109 101 | 255 | 74 111 104 110 | 255 | 108 97 115 116 110 97 109 101 | 255 | 68 111 101 | 254
```

### Null Values
Null values are represented by the special byte `FD (253)`.
They are used to indicate that a value is missing or not applicable.

Example:
```json
{"Name": "John Doe", "company": null}
```
Is encoded as:
```
252 | 78 97 109 101 | 255 | 74 111 104 110 32 68 111 101 | 255 | 99 111 109 112 97 110 121 | 255 | 253 | 254
```

### Binary Data
Binary data is stored in LOADS as a base64 encoded string. To indicate that a value is binary data,
it is prefixed with the special byte `FB`. The base64 encoded string is represented as a normal string,
using the characters `A-Z`, `a-z`, `0-9`, `-`, `_` (base64url encoding, RFC 4648 section 5). 
Base64url encoding is used instead of the standard base64 encoding, because the `+` character is
used for type prefixes.
Since these characters are all 8-bit aligned, the `=` character can be omitted.

In some cases, the binary data needs a type to be specified. This can be done in a similar fashion to
what is suggested for RSV in [this issue](https://github.com/Stenway/RSV-Specification/issues/1):

> If the data type would have to be derived from this binary data, the base64 value could be prefixed (after the \FB) 
> by a string surrounded by non-base64 characters, to signify the data type, like (i32) for 32-bit integers.
> 
> Example:
> ```
> 251 | 40, 105, 51, 50, 41 | 83, 90, 89, 67, 48, 103, 61, 61 | 255 | 253
> \FB |  type: 32-bit int   |         value: SZYC0g==         | \FF | \FD
> ```
> ...which would represent a single integer (int32) value that equals 1234567890.
> 
> Or you could use something more simple, but restrictive typing system, 
> that uses a single non-base64 character to define the type, followed by a single character for the size.
> ```
> 251 | 35, 52 | 83, 90, 89, 67, 48, 103, 61, 61 | 255 | 253
> \FB |   #4   |             SZYC0g==            | \FF | \FD
> ```
> ...where # defines an integer and 4 defines a size of 4 bytes (32 bit): `1234567890`
> ```
> 251 | 35, 52 | 81, 69, 107, 80, 50, 119, 61, 61 | 255 | 253
> \FB |   ~4   |             QEkP2w==             | \FF | \FD
> ```
> ...where ~ defines a floating point value and 4 defines a size of 4 bytes (32 bit): `3.141592...`

This same principle can be applied to LOADS. This is possible, because base64 encoded strings only use
the aforementioned characters. Therefore, if the base64 encoded string is prefixed by a non-base64 character,
it can be used to indicate the type of the binary data.

#### Integers
Integers are binary data that represent a number. They can be signed or unsigned. 

The type can be indicated by prefixing the base64 encoded string with 
`#1` or `+1` for a 1-byte (8-bit) signed or unsigned integer, 
`#2` or `+2` for a 2-byte (16-bit) signed or unsigned integer, 
`#4` or `+4` for a 4-byte (32-bit) signed or unsigned integer, 
or `#8` or `+8` for an 8-byte (64-bit) signed or unsigned integer respectively.

Any zero bytes at the start of the binary data can be omitted, since they don't change the value.

Example:
```json
{"id": 1234567890}
```
Is encoded as:
```
252 | 105 100 | 255 | 251 | 35 52 | 83 90 89 67 48 103 | 254
 {  |   id    | :   |<bin>|  #4   |       SZYC0g       |  }
```

#### Floating point numbers
Floating point numbers are binary data that represent a number with a fractional part or more specifically,
a number that can be represented in scientific notation with a mantissa and an exponent.

The type can be indicated by `~4` for a 4-byte (32-bit) floating point number, or `~8` for an 8-byte (64-bit) floating point number.

Example:
```json
{"pi": 3.141592653589793}
```
Is encoded as:
```
252 | 112 105 | 255 | 251 | 126 52 | 81 69 107 80 50 119 | 254
 {  |   pi    | :   |<bin>|   ~4   |       QEkP2w        |  }
```

#### Dates
Dates are binary data that represent a date and/or time. They can be represented in many different ways,
but the most common way is to use the Epoch timestamp, which is the time since `1970-01-01 00:00:00`.
If date formatting and time zones are important, it is recommended to use a string instead of binary data.

The type can be indicated by `@4` for a 4-byte (32-bit) timestamp giving the seconds since the epoch, 
`@8` for an 8-byte (64-bit) timestamp, given in milliseconds since the epoch,
or `@C` or `@c` (c is hex for 12) for a 12-byte (96-bit) timestamp, given in nanoseconds since the epoch 
(where the upper 8 bytes are the seconds and the lower 4 bytes are the nanosecond fraction).

Any zero bytes at the start of the binary data can be omitted, since they don't change the value.

Example 1:
```json
{"start": 1717967811}
```
Is encoded as:
```
252 | 115 116 97 114 116 | 255 | 251 | 64 52 | 90 109 89 98 119 119 | 254
 {  |       start        | :   |<bin>|  @4   |        ZmYbww        |  }
```

Example 2:
```json
{"start": [1718315521, 191598900]}
```
Is encoded as:
```
252 | 115 116 97 114 116 | 255 | 251 | 64 67 | 66 109 97 50 111 66 116 114 107 84 81 | 254
 {  |       start        | :   |<bin>|  @C   |              Bma2oBtrkTQ              |  }
```

#### Booleans
Booleans are binary data that represent a true or false value. They can be represented in many different ways
and are hard to represent efficiently in binary form, since only 1 bit is needed, but the minimum size of a
value is usually 8, 16, 32 or even 64 bits. Since LOADS uses 6-bit values for binary data, represented as 8-bit characters, 
it would be possible to represent upto 6 booleans in a single Base64 encoded character, 
though it will usually be the case that only 1 boolean will be stored.

The type can be indicated by `!1` for a single boolean value, or `!2`-`!6` for 2 to 6 boolean values. 
This removes the need for creating an array when dealing with upto 6 boolean values.

To save a byte, the value can also be represented by specifying the type as `!t` for true and `!f` for false, 
with 0 bytes in the Base64 encoded string.

Example using `!2`:
```json
{"active": [true, false]}
```
Is encoded as:
```
252 | 97 99 116 105 118 101 | 255 | 251 | 33 50 |  2          | 254
 {  |   active              | :   |<bin>|  !2   | true, false |  }
```

Example using `!t`:
```json
{"active": true}
```
Is encoded as:
```
252 | 97 99 116 105 118 101 | 255 | 251 | 33 116 | 254
 {  |   active              | :   |<bin>|  !t    |  }
```

#### General data type notation
The previous type definitions are for the most common data types, optimized to only require 2 bytes for the type definition.
Sometimes it is necessary to store other data types, 128-bit floating point numbers or specific file types like PNGs or MP3s.

In this case, the type can be indicated by prefixing the base64 encoded string with 
a `(` character, followed by a string, followed by a `)` character, where the string represents the type of the binary data.

This string can use any valid UTF-8 character, except for the `)` character, which is used to indicate the end of the type string.
In most cases, it will represent a MIME type or a file extension. It can also represent a custom type, like `int32` or `float64` 
or a domain-specific type like `user` or `product`. To be able to encode and decode these types, 
the implementation needs to supply a mapping to and from base64 for these types.

Example:
```json
{"avatar": "data:image/png;base64,<base64>"} 
```
Is encoded as:
```
252 | 97 118 97 116 97 114 | 255 | 251 | 40 105 109 97 103 101 47 112 110 103 41 | <base64url without padding> | 254    
 {  |        avatar        | :   |<bin>|               (image/png)               | <base64url without padding> |  }
```

### Metadata
> This section is incomplete and subject to change.

Metadata is described in an object at start of the data structure

#### Version
v -> version number

TODO, specify format (incrementing number? or semantic versioning? How many bytes?)

#### Boolean order
b -> Choose between Little-endian (LE) or big-endian (BE)

TODO specify default

#### Binary encoding
e -> Choose encoding for binary section: Base64 (default) or Base128

TODO: specify format for Base128 representation (ASCII? UTF-7? anything else with 7 bits?)


## Parsing to human-readable format like JSON
> This section is incomplete and subject to change.

The result would technically be a JSON lines file, where the first line describes the metadata and the following line describes the data.