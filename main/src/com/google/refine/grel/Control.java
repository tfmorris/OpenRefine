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

import com.google.refine.expr.Evaluable;

/**
 * Interface of GREL controls such as if, forEach, forNonBlank, with. A control can decide which part of the code to
 * execute and can affect the environment bindings. Functions, on the other hand, can't do either.
 */
public interface Control {

    /**
     * Invoke this control with the given arguments and the variable bindings.
     *
     * @param bindings variable bindings to be used for this invocation.
     * @param arguments arguments to be used for this invocation
     * @return return value as one of our data types or an {@link com.google.refine.expr.EvalError}
     */
    public Object call(Properties bindings, Evaluable[] arguments);

    /**
     * Check argument count and types for validity, but does not evaluate them.
     *
     * @param arguments arguments to be checked
     * @return an error string if any problems are found or null if everything is correct
     */
    public String checkArguments(Evaluable[] arguments);

    /**
     * Return a natural language description of the control to be used in online help.
     *
     * @return string containing the localized description
     */
    @JsonProperty("description")
    public String getDescription();

    /**
     * Return a brief description of name and type for all arguments.
     *
     * @return a single localized string describing all arguments.
     */
    @JsonProperty("params")
    @JsonInclude(Include.NON_EMPTY)
    default public String getParams() {
        return "";
    }

    /**
     * Return the type of the return value as a localized string, to be used in online help.
     *
     * @return a localized string describing the type of the return value.
     */
    @JsonProperty("returns")
    public String getReturns();
}
