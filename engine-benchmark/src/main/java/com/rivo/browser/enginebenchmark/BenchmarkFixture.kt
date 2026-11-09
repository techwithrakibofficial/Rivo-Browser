package com.rivo.browser.enginebenchmark

import android.net.Uri

object BenchmarkFixture {
    const val READY_PREFIX = "RIVO_BENCH_READY_"
    fun readyTitle(tabId: Int) = "${READY_PREFIX}${tabId}"

    fun dataUri(tabId: Int): String {
        val html = """
            <!doctype html><html><head><meta charset="utf-8">
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Rivo benchmark loading</title>
            <style>body{font-family:sans-serif;margin:18px;background:#f5f7fa}
            li{padding:4px 0;border-bottom:1px solid #ddd}</style></head>
            <body><h1>Rivo engine benchmark</h1><p>Offline workload: $tabId</p>
            <ol id="items"></ol><script>
            window.addEventListener('load',function(){
              const list=document.getElementById('items');
              for(let i=0;i<400;i++){const row=document.createElement('li');
                row.textContent='Deterministic content row '+i;list.appendChild(row);}
              document.title='RIVO_BENCH_READY_$tabId';
            });
            </script></body></html>
        """.trimIndent()
        return "data:text/html;charset=utf-8," + Uri.encode(html)
    }
}
