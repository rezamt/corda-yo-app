package com.template.states

import com.template.contracts.YoContract
import net.corda.core.contracts.*
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table


// *********
// * State *
// *********
@BelongsToContract(YoContract::class)
data class YoState(val origin: Party,
                   val target: Party,
                   val yo: String = "Yo!") : ContractState, QueryableState {
    override val participants get() = listOf(origin, target)
    override fun toString() = "${origin.name}: $yo"
    override fun supportedSchemas() = listOf(YoSchemaV1)
    override fun generateMappedObject(schema: MappedSchema) = YoSchemaV1.PersistentYoState(
            origin.name.toString(), target.name.toString(), yo)

    object YoSchema

    object YoSchemaV1 : MappedSchema(YoSchema.javaClass, 1, listOf(PersistentYoState::class.java)) {
        @Entity
        @Table(name = "yos")
        class PersistentYoState(
                @Column(name = "origin")
                var origin: String = "",
                @Column(name = "target")
                var target: String = "",
                @Column(name = "yo")
                var yo: String = ""
        ) : PersistentState()
    }
}