package io.exam.intellij.plugin.action

class MakeHttp : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private val BLOCK =
            """
            .HTTP API check
            [e-http=]
            --
            .Given request:
            [source,httprequest]
            ----
            GET /mirror/headers
            Content-Type: application/json
            Authorization: token
            Accept-Language: ru
            Cookie: cookie1=c1; cookie2=c2
            ----
            
            .Expected response:
            [source,httprequest]
            ----
            200 OK
            Content-Type: application/json
            
            {
              "GET": "/mirror/headers",
              "Authorization": "token",
              "Accept-Language": "ru",
              "cookies": {
                "cookie1": "c1",
                "cookie2": "c2"
              }
            }
            ----
            --
            """.trimIndent()
    }

}