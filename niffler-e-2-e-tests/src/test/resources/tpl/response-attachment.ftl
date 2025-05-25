<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<#assign objectMapper = "freemarker.template.utility.ObjectConstructor"?new()("com.fasterxml.jackson.databind.ObjectMapper")>
<#function prettyJson input>
    <#attempt>
        <#return objectMapper.readTree(input).toPrettyString()>
        <#recover>
            <#return input>
    </#attempt>
</#function>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="https://yandex.st/highlightjs/8.0/styles/github.min.css">
    <script src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/http.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/json.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>
    <style>
        pre {
            white-space: pre-wrap;
        }
    </style>
</head>
<body>
<h5>HTTP Response</h5>
<div>
    <pre><code class="http">HTTP/1.1 ${data.responseCode!"<unknown status>"}
            <#if data.headers??>
                <#list data.headers?keys as key>
                    ${key}: ${data.headers[key]}
                </#list>
            </#if>
</code></pre>

    <#if data.body?has_content>
        <h5>Response Body</h5>
        <pre><code class="json">${prettyJson(data.body)}</code></pre>
    </#if>
</div>
</body>
</html>
