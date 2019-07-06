package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator : FlowLogic<Unit>() {

    private val CREATING_CONTRACT = ProgressTracker.Step(
            "Preparing contract")

    private val SUBMIT_CONTRACT = ProgressTracker.Step(
            "Submitting contract to Purchaser")

    private val WAITING_FOR_SIGNATURE = ProgressTracker.Step(
            "Waiting for Purchaser to approve contract")

    override val progressTracker = ProgressTracker(
            CREATING_CONTRACT,
            SUBMIT_CONTRACT,
            WAITING_FOR_SIGNATURE

    )

    @Suspendable
    override fun call() {
        logger.info("Logging within a flow.")

        val me = serviceHub.myInfo.legalIdentities[0]

        logger.info("Logging me: "+ me.name)

        if (serviceHub.networkMapCache.notaryIdentities.isEmpty()) {
            logger.error("Failed to get Notray: ")
        }

        val notary = serviceHub.networkMapCache.notaryIdentities[0]


        logger.info("Logging notary: " + notary.name)

        progressTracker.currentStep = CREATING_CONTRACT

        progressTracker.currentStep = SUBMIT_CONTRACT

        progressTracker.currentStep = WAITING_FOR_SIGNATURE

        // Initiator flow logic goes here.
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {

        progressTracker.currentStep = ProgressTracker.Step(label = "Responder Called")

        // Responder flow logic goes here.
    }
}
