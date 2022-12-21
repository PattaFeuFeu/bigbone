package social.bigbone.rx.extensions

import io.reactivex.Single

fun <T> single(f: () -> T): Single<T> {
    return Single.create {
        try {
            val result = f()
            it.onSuccess(result)
        } catch(throwable: Throwable) {
            it.onErrorIfNotDisposed(throwable)
        }
    }
}
