package com.github.curiousoddman.rgxgen.generator.visitors;

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

import com.github.curiousoddman.rgxgen.generator.nodes.*;

public interface NodeVisitor {
    void visit(SymbolSet node);

    void visit(Choice node);

    void visit(FinalSymbol node);

    void visit(Repeat node);

    void visit(Sequence node);

    void visit(NotSymbol node);

    void visit(GroupRef node);

    void visit(Group node);
}
