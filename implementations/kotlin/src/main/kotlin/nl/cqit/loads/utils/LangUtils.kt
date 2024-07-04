package nl.cqit.loads.utils

 fun<V, T, R> Function1<V, T>.andThen(func: T.() -> R): (V) -> R {
    return { v: V -> func(this(v)) }
}