package com.example.familytree.data

/**
 * Singleton class to manage the mapping of family members by unique IDs.
 *
 * This class maintains a mapping between unique integer IDs and their corresponding
 * [FamilyMember] objects. It ensures that each family member is assigned a unique ID
 * and provides methods for accessing and updating this mapping.
 *
 * The singleton pattern is used to ensure a single instance of this class throughout the application.
 */
class MemberMapByID private constructor() {

    // An integer that keeps track of the last ID added,
    // in order to know what ID a new member should get.
    private var lastIDAdded = 0

    // Map to store family members by ID
    val idMap: MutableMap<Int, FamilyMember> = mutableMapOf()

    // Static variable to hold the single instance of MemberMapByID
    companion object {
        // Singleton instance
        @Volatile
        private var instance: MemberMapByID? = null

        /**
         * Provides access to the single instance of MemberMapByID.
         */
        fun getInstance(): MemberMapByID {
            return instance ?: synchronized(this) {
                instance ?: MemberMapByID().also { instance = it }
            }
        }
    }

    /**
     * Provides a new unique ID for a member to be added to the tree.
     * @return the new unique ID.
     */
    fun getNewID(): Int {
        return ++lastIDAdded
    }

    /**
     * Adds a new member to the ID map.
     * @param familyMember the family member to be added.
     */
    fun addNewMemberToIDMap(familyMember: FamilyMember) {
        idMap[familyMember.ID] = familyMember
    }

    /**
     * Checks if a member is already in the tree.
     * @param familyMember the family member to check.
     * @return true if the member is in the map, false otherwise.
     */
    fun isMemberInTree(familyMember: FamilyMember): Boolean {
        return idMap.containsKey(familyMember.ID)
    }
}
