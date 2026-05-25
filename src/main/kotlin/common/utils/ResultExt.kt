package common.utils

inline fun <T, R> Result<T>.mapChain(transform: (T) -> Result<R>): Result<R> = fold(
    onSuccess = transform,
    onFailure = { Result.failure(it) },
)