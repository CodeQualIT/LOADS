package nl.cqit.loads.utils

 fun<V, T, R> Function1<V, T>.andThen(func: T.() -> R): (V) -> R {
    return { func(this(it)) }
}