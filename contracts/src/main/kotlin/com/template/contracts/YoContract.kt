package com.template.contracts

import com.template.states.YoState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

// ************
// * Contract *
// ************

// Contract and state.
const val YO_CONTRACT_ID = "com.template.contracts.YoContract"

class YoContract : Contract {

    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "YoContract"
    }

    // Command.
    class Send : TypeOnlyCommandData()

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.

    // Contract code.
    override fun verify(tx: LedgerTransaction) = requireThat {
        val command = tx.commands.requireSingleCommand<Send>()
        "There can be no inputs when Yo'ing other parties." using (tx.inputs.isEmpty())
        "There must be one output: The Yo!" using (tx.outputs.size == 1)
        val yo = tx.outputsOfType<YoState>().single()
        "No sending Yo's to yourself!" using (yo.target != yo.origin)
        "The Yo! must be signed by the source and target together only may sign IOU issue transaction." using
                (command.signers.toSet() == yo.participants.map { it.owningKey }.toSet())

    }
}