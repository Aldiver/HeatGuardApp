package com.example.heatguardapp.utils

import com.example.heatguardapp.api.models.UserInfoApi
import com.example.heatguardapp.data.KDTreeNode
import kotlin.math.abs
class KDTree(private val points: List<UserInfoApi>) {

    private var root: KDTreeNode? = null

    // Build the KD-tree
    fun buildTree(): KDTreeNode? {
        return buildTreeRecursive(points, 0)
    }

    private fun buildTreeRecursive(points: List<UserInfoApi>, depth: Int): KDTreeNode? {
        if (points.isEmpty()) {
            return null
        }

        val dimension = depth % UserInfoApi::class.java.declaredFields.size
        points.sortedBy { it::class.java.declaredFields[dimension].getFloat(it) }
        val medianIndex = points.size / 2
        val medianPoint = points[medianIndex]

        val left = buildTreeRecursive(points.subList(0, medianIndex), depth + 1)
        val right = buildTreeRecursive(points.subList(medianIndex + 1, points.size), depth + 1)

        return KDTreeNode(medianPoint, left, right, dimension)
    }

    private fun buildTreeRecursiveInternal(points: List<UserInfoApi>, depth: Int): KDTreeNode? {
        if (points.isEmpty()) {
            return null
        }

        val dimension = depth % UserInfoApi::class.java.declaredFields.size
        points.sortedBy { it::class.java.declaredFields[dimension].getFloat(it) }
        val medianIndex = points.size / 2
        val medianPoint = points[medianIndex]

        val left = buildTreeRecursiveInternal(points.subList(0, medianIndex), depth + 1)
        val right = buildTreeRecursiveInternal(points.subList(medianIndex + 1, points.size), depth + 1)

        return KDTreeNode(medianPoint, left, right, dimension)
    }

    // Search for nearest neighbor
    fun findNearestNeighbor(currValue: UserInfoApi): UserInfoApi? {
        return findNearestNeighborRecursive(root, currValue, 0)
    }

    private fun findNearestNeighborRecursive(node: KDTreeNode?, target: UserInfoApi, depth: Int): UserInfoApi? {
        if (node == null) {
            return null
        }

        val dimension = depth % UserInfoApi::class.java.declaredFields.size
        val currentNode = node.point

        val nextNode: KDTreeNode?
        val otherNode: KDTreeNode?

        if (target::class.java.declaredFields[dimension].getFloat(target) < currentNode::class.java.declaredFields[dimension].getFloat(currentNode)) {
            nextNode = node.left
            otherNode = node.right
        } else {
            nextNode = node.right
            otherNode = node.left
        }

        var best = currentNode
        val nextBest = findNearestNeighborRecursive(nextNode, target, depth + 1)

        if (nextBest != null && isBetterMatch(nextBest, target, best)) {
            best = nextBest
        }

        if (otherNode != null && isBetterMatch(currentNode, target, best)) {
            val otherBest = findNearestNeighborRecursive(otherNode, target, depth + 1)
            if (otherBest != null && isBetterMatch(otherBest, target, best)) {
                best = otherBest
            }
        }

        return best
    }

    private fun isBetterMatch(candidate: UserInfoApi, target: UserInfoApi, currentBest: UserInfoApi): Boolean {
        // Check if age, bmi, and skinRes are exactly the same
        if (candidate.age != target.age || candidate.bmi != target.bmi || candidate.skinRes != target.skinRes) {
            return false
        }

        // Check if other attributes are within +/- 5% of the target
        val fields = UserInfoApi::class.java.declaredFields
        for (field in fields) {
            if (field.name == "heatstroke") continue // Skip checking heatstroke
            val currentValue = field.getFloat(candidate)
            val targetValue = field.getFloat(target)
            val percentageDifference = abs(currentValue - targetValue) / targetValue
            if (percentageDifference > 0.05) {
                return false
            }
        }

        return true
    }
}
