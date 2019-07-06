package com.template.webserver

import com.template.states.YoState
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.internal.toX500Name
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.NodeInfo
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x500.style.BCStyle
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController





/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val rpcOps = rpc.proxy


    private val me = rpcOps.nodeInfo().legalIdentities.first().name


    fun X500Name.toDisplayString() : String  = BCStyle.INSTANCE.toString(this)

    /** Helpers for filtering the network map cache. */
    private fun isNotary(nodeInfo: NodeInfo) = rpcOps.notaryIdentities().any { nodeInfo.isLegalIdentity(it) }

    private fun isMe(nodeInfo: NodeInfo) = nodeInfo.legalIdentities.first().name == me

    private fun isNetworkMap(nodeInfo : NodeInfo) = nodeInfo.legalIdentities.single().name.organisation == "Network Map Service"



    @GetMapping(value = "/version", produces = arrayOf("text/plain"))
    private fun version(): String {
        return "Yo application version 0.1"
    }


    @GetMapping(value = "/me", produces= arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun whoami() = mapOf("me" to me.toString())


    @GetMapping(value = "/peers", produces= arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun peers(): Map<String, List<String>> {
        return mapOf("peers" to rpcOps.networkMapSnapshot()
                .filter { isNotary(it).not() && isMe(it).not() && isNetworkMap(it).not() }
                .map { it.legalIdentities.first().name.toX500Name().toDisplayString() })
    }


    @GetMapping(value = "/yos", produces= arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun yos(): List<StateAndRef<ContractState>> {
        // Filter by state type: YoState.
        return rpcOps.vaultQueryBy<YoState>().states
    }

}