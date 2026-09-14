/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.yg.mileage.annotations

/**
 * Annotation used to mark MyMileage APIs that have been deprecated and are scheduled for removal.
 *
 * This annotation is configured with [RequiresOptIn.Level.ERROR] to strictly prevent the
 * accidental use of legacy code. Developers must explicitly opt-in to use these APIs by
 * annotating the call site with [UsesDeprecatedMyMileageApi].
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR, // Use ERROR to enforce strict compliance
    message = "This MyMileage API is deprecated and is likely to be removed in the future."
)
@Retention(AnnotationRetention.BINARY)
annotation class DeprecatedMyMileageApi

/**
 * Indicates that the annotated element uses APIs marked with [DeprecatedMyMileageApi].
 *
 * Applying this annotation acknowledges the use of legacy MyMileage components
 * that are scheduled for removal and suppresses associated compiler warnings
 * within the annotated scope.
 */
@OptIn(DeprecatedMyMileageApi::class) // This links the two
@Retention(AnnotationRetention.BINARY)
annotation class UsesDeprecatedMyMileageApi
