package com.example.data

import java.math.BigDecimal
import java.math.RoundingMode

data class Transfer(
    val from: String,
    val to: String,
    val amount: BigDecimal
)

data class ParticipantBalance(
    val name: String,
    val spent: BigDecimal,
    val balance: BigDecimal // Positive means they should receive, negative means they owe
)

data class SplitResult(
    val totalSpent: BigDecimal,
    val quota: BigDecimal,
    val balances: List<ParticipantBalance>,
    val transfers: List<Transfer>
)

object SplitCalculator {
    fun calculateSplit(participants: List<String>, gastos: List<Gasto>): SplitResult {
        if (participants.isEmpty()) {
            return SplitResult(BigDecimal.ZERO, BigDecimal.ZERO, emptyList(), emptyList())
        }

        // Calculate total spent, scaled to 0 decimal places (since we want clean integer splits)
        val totalSpent = gastos.fold(BigDecimal.ZERO) { acc, gasto ->
            acc + gasto.amount
        }.setScale(0, RoundingMode.HALF_UP)

        val n = participants.size
        val (quotaBase, remainderBigDecimal) = totalSpent.divideAndRemainder(BigDecimal(n))
        val remainder = remainderBigDecimal.toInt()

        // Distribute remainder to the first `remainder` participants to ensure quota sum matches totalSpent perfectly
        val participantQuotas = participants.mapIndexed { index, name ->
            val extra = if (index < remainder) BigDecimal.ONE else BigDecimal.ZERO
            name to (quotaBase + extra)
        }.toMap()

        // Calculate spent for each participant, scaled to 0
        val spentMap = participants.associateWith { name ->
            val totalForP = gastos.filter { it.payerName.equals(name, ignoreCase = true) }
                .fold(BigDecimal.ZERO) { acc, g -> acc + g.amount }
            totalForP.setScale(0, RoundingMode.HALF_UP)
        }

        // Calculate balances
        val balances = participants.map { name ->
            val spent = spentMap[name] ?: BigDecimal.ZERO
            val quota = participantQuotas[name] ?: quotaBase
            val balance = spent - quota
            ParticipantBalance(name, spent, balance)
        }

        // Separate into debtors (balance < 0) and creditors (balance > 0)
        // Since we are working with exact integer decimals (BigDecimal with scale 0),
        // we can check strictly < 0 or > 0.
        val debtors = balances.filter { it.balance < BigDecimal.ZERO }
            .map { it.name to -it.balance }
            .toMutableList()

        val creditors = balances.filter { it.balance > BigDecimal.ZERO }
            .map { it.name to it.balance }
            .toMutableList()

        // Sort descending to settle largest amounts first
        debtors.sortByDescending { it.second }
        creditors.sortByDescending { it.second }

        val transfers = mutableListOf<Transfer>()

        while (debtors.isNotEmpty() && creditors.isNotEmpty()) {
            val debtor = debtors[0]
            val creditor = creditors[0]

            val debtorName = debtor.first
            val debtorOwes = debtor.second

            val creditorName = creditor.first
            val creditorClaim = creditor.second

            val amount = debtorOwes.min(creditorClaim)
            transfers.add(Transfer(from = debtorName, to = creditorName, amount = amount))

            // Update remaining
            val remDebtor = debtorOwes - amount
            val remCreditor = creditorClaim - amount

            if (remDebtor > java.math.BigDecimal.ZERO) {
                debtors[0] = debtorName to remDebtor
            } else {
                debtors.removeAt(0)
            }

            if (remCreditor > java.math.BigDecimal.ZERO) {
                creditors[0] = creditorName to remCreditor
            } else {
                creditors.removeAt(0)
            }
        }

        return SplitResult(totalSpent, quotaBase, balances, transfers)
    }
}
