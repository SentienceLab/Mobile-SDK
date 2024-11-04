package io.metamask.androidsdk

data class EthereumRequest(
    override var id: String = TimeStampGenerator.timestamp(),
    override val method: String,
    override val params: Any? = null
) : RpcRequest()