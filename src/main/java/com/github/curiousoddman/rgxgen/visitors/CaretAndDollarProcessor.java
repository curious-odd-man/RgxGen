package com.github.curiousoddman.rgxgen.visitors;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/

import com.github.curiousoddman.rgxgen.nodes.*;

/**
 * A visitor that while visiting nodes should evaluate which nodes cannot be a part of generated text due to caret or dollar placement.
 * Examples:
 * (^a)+ -> only matches single `a` character. i.e. max repetition is 1
 * (a$|c)x -> ax cannot be, only cx
 * x(a|^c) -> only xa is valid
 */
public class CaretAndDollarProcessor implements NodeVisitor {
    @Override
    public void visit(SymbolSet node) {

    }

    @Override
    public void visit(Choice node) {

    }

    @Override
    public void visit(FinalSymbol node) {

    }

    @Override
    public void visit(Repeat node) {

    }

    @Override
    public void visit(Sequence node) {

    }

    @Override
    public void visit(NotSymbol node) {

    }

    @Override
    public void visit(GroupRef node) {

    }

    @Override
    public void visit(Group node) {

    }
}
