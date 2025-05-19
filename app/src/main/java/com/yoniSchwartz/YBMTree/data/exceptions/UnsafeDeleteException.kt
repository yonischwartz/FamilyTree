package com.yoniSchwartz.YBMTree.data.exceptions

class UnsafeDeleteException(memberId: String) : Exception(
    "Deleting member with ID '$memberId' would split the family tree into multiple components, which is not allowed."
)
