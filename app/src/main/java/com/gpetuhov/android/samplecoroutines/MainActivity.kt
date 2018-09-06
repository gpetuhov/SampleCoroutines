package com.gpetuhov.android.samplecoroutines

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        helloText.setOnClickListener {
            // Here we start a coroutine on a background thread
            launch {
                delay(2000)

                // Here we start a coroutine on the main thread
                launch(UI) {
                    toast("Hello with a delay!")
                }
            }
        }

        helloText.setOnLongClickListener {
            // This BLOCKS the main thread!!!
            runBlocking {
                delay(10000)
            }

            toast("Another hello with a delay!")

            true
        }

        button.setOnClickListener {
            // And this does NOT block the main thread.
            // Because we start a new coroutine on the main thread.
            // And while the coroutine is suspended by the delay(),
            // the main thread itself is NOT.
            launch(UI) {
                delay(2000)
                toast("Hello from button with delay")
            }
        }

        button.setOnLongClickListener {
            // With launch(UI) we can wait for the result of a long-running operation
            // on the main thread without blocking the thread itself.
            // BUT the operation itself must run in another coroutine on the background thread
            // (because coroutine started with launch(UI) executes on the main thread,
            // so in this case the long-running operation will block the main thread).
            // We can do it like in the first example above or using async like this.

            // This starts to execute immediately
            val result = async {
                delay(2000)
                100
            }

            launch(UI) {
                // And here we are waiting for the result of the async() on the main thread.
                // Until result is ready, the coroutine is suspended, but the thread itself is NOT.
                // When the result is ready, the coroutine continues execution.
                toast("Result is ${result.await()}")
            }

            true
        }
    }
}
