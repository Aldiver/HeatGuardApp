package com.example.heatguardapp.data

import com.example.heatguardapp.api.models.UserInfoApi

data class KDTreeNode(
    val point: UserInfoApi,
    val left: KDTreeNode?,
    val right: KDTreeNode?,
    val splitDimension: Int // Dimension along which the space is split
)