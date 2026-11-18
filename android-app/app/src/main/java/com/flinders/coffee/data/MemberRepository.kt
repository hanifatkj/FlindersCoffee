package com.flinders.coffee.data

data class MemberModel(
    val code: String,
    val name: String,
    val phone: String,
    var stamps: Int,
    val maxStamps: Int = 10,
    var isLocked24h: Boolean = false,
    var lockedUntil: String? = null,
    var lastRedemptionTime: String? = null,
    var lastRedemptionCode: String? = null
)

object MemberRepository {

    private val members = mutableListOf(
        MemberModel(
            code = "FLN-482913",
            name = "Rina",
            phone = "0812-3456-7890",
            stamps = 10,
            isLocked24h = false
        ),
        MemberModel(
            code = "FLN-482910",
            name = "Bima",
            phone = "0813-8899-1122",
            stamps = 10,
            isLocked24h = false
        ),
        MemberModel(
            code = "FLD-001",
            name = "Andi Pratama",
            phone = "0852-7711-2233",
            stamps = 7,
            isLocked24h = false
        ),
        MemberModel(
            code = "FLN-991204",
            name = "Sarah",
            phone = "0821-4455-6677",
            stamps = 0,
            isLocked24h = true,
            lockedUntil = "Besok, 11.50 WITA"
        )
    )

    fun findMember(query: String): MemberModel? {
        val clean = query.trim().uppercase()
        return members.find {
            it.code.equals(clean, ignoreCase = true) ||
            clean.contains(it.code) ||
            it.phone.contains(clean) ||
            it.name.equals(clean, ignoreCase = true)
        }
    }

    fun redeemReward(memberCode: String, ownerName: String = "Nadia · Owner"): Pair<Boolean, String> {
        val member = findMember(memberCode) ?: return Pair(false, "Member tidak ditemukan")
        if (member.isLocked24h) {
            return Pair(false, "Penukaran hadiah terkunci 24 jam.")
        }
        if (member.stamps < member.maxStamps) {
            return Pair(false, "Stamp belum lengkap (${member.stamps}/${member.maxStamps}). Belum berhak tukar.")
        }

        // Reset stamp to 0 and lock 24 hours
        member.stamps = 0
        member.isLocked24h = true
        member.lockedUntil = "Besok, 24 jam ke depan"
        val redemptionCode = "#RDM-" + (100..999).random()
        member.lastRedemptionCode = redemptionCode
        member.lastRedemptionTime = "Hari ini · 11.50 WITA"

        return Pair(true, redemptionCode)
    }

    fun addStamp(memberCode: String): Pair<Boolean, String> {
        val member = findMember(memberCode) ?: return Pair(false, "Member tidak ditemukan")
        if (member.stamps >= member.maxStamps) {
            return Pair(false, "Stamp sudah maksimal (10/10). Siap ditukar hadiah!")
        }
        member.stamps += 1
        return Pair(true, "1 Stamp berhasil ditambahkan (${member.stamps}/${member.maxStamps})")
    }
}
