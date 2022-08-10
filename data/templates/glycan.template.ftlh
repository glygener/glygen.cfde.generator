## Glycan Details

![Image](https://api.glygen.org/glycan/image/${glyTouCanId})

**GlyTouCan Accession**: [${glyTouCanId}](https://www.glygen.org/glycan/${glyTouCanId})  
**PubChem CID**: <#if pubChemId??>${pubChemId}<#else>ID not found</#if>  
<#setting number_format=",##0.00">
<#setting locale="en_US">
<#if mass??>**Monoisotopic Mass**: ${mass} Da  </#if>
<#if composition??>**Composition**: ${composition}  </#if>
**Motifs**: <#list motifs as motif>${motif.name}<#sep>, </#list>  
**Organism**: <#list organism as species>${species.name}<#sep>, </#list>  


More information for glycan [${glyTouCanId}](https://www.glygen.org/glycan/${glyTouCanId}) is available on GlyGen