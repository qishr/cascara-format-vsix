package io.github.qishr.cascara.format.vsix;

import io.github.qishr.cascara.common.annotation.SchemaDefinition;

@SchemaDefinition
public sealed class Contribution permits
    ThemeContribution,
    LanguageContribution,
    GrammarContribution
{
    // Nothing to see here
}