package com.android.singledownload

import io.reactivex.observers.ResourceObserver

abstract class BaseObserver<T> : ResourceObserver<T> {

    protected constructor() {

    }
    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {

    }

    override fun onComplete() {

    }

}
