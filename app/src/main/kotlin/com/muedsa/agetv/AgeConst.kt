package com.muedsa.agetv

import com.muedsa.uitl.decodeBase64ToStr

const val AgeMobileUrlBase64 = "aHR0cHM6Ly9tLmFnZWRtLm9yZy8="
val AgeMobileUrl = AgeMobileUrlBase64.decodeBase64ToStr()
const val AgeMobileApiUrlBase64 = "aHR0cHM6Ly9hcGkuYWdlZG0ub3JnLw0K"
val AgeMobileApiUrl = AgeMobileApiUrlBase64.decodeBase64ToStr()