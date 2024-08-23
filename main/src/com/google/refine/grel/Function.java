/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.grel;

import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Interface implemented by all GREL functions. When a function is called, its arguments have already been evaluated down into non-error
 * values.
 */
public interface Function {

    /**
     * Invoke the function with the given variable bindings and arguments and return the result.
     *
     * @param bindings bindings for all defined variables and pseudovariables
     * @param parameters parameters for arguments being passed in by the caller
     * @return return value as one of our internal datatypes
     */
    Object call(Properties bindings, Object[] parameters);

    /**
     * Returns a natural language description of the function to be used in online help.
     *
     * @return string containing the description.
     */
    @JsonProperty("description")
    String getDescription();

    /**
     * Returns a natural language description of the parameters taken by the function.
     * @return string containing the description of the parameters.
     */
    @JsonProperty("params")
    @JsonInclude(Include.NON_EMPTY)
    default String getParams() {
        return "";
    }

    /**
     * Returns a natural language description of the return value of the function.
     *
     * @return string containing the description of the return value
     */
    @JsonProperty("returns")
    String getReturns();
}
