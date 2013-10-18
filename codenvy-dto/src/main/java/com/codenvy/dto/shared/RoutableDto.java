// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.codenvy.dto.shared;

/** Base interface for all DTOs that adds a type tag for routing messages. */
public interface RoutableDto {
    public static final int    SERVER_ERROR      = -1;
    public static final int    NON_ROUTABLE_TYPE = -2;
    public static final String TYPE_FIELD        = "_type";

    /** Every DTO needs to report a type for the purposes of routing messages on the client. */
    public int getType();
}