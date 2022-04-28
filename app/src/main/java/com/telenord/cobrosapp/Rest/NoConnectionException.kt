package com.telenord.cobrosapp.Rest

import java.io.IOException

class NoConnectionException: IOException()
{
    override val message: String?
        get() = "No Connection"
}