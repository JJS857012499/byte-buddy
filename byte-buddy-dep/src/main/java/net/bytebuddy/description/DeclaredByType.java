/*
 * Copyright 2014 - Present Rafael Winterhalter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.bytebuddy.description;

import net.bytebuddy.description.type.TypeDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface represents all elements that can be declared within a type, i.e. other types and type members.
 */
public interface DeclaredByType {

    /**
     * Returns the declaring type of this instance.
     *
     * @return The declaring type or {@code null} if no such type exists.
     */
    @Nullable
    TypeDefinition getDeclaringType();

    /**
     * Indicates that this element must always be declared by a type.
     */
    interface WithMandatoryDeclaration extends DeclaredByType {

        /**
         * {@inheritDoc}
         */
        @Nonnull
        TypeDefinition getDeclaringType();
    }
}
