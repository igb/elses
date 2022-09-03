<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:svg="http://www.w3.org/2000/svg"  version="1.0">
  <xsl:output method="xml"/>

  <xsl:template match="files">
    <svg:svg width="420.0mm"
	     height="291.0mm"
	     viewBox="0 0 420.0 291.0"
	     version="1.1">
    <xsl:for-each select="file">
      <xsl:variable name="svg"><xsl:value-of select="."/></xsl:variable>
      <xsl:apply-templates select="document($svg)"/>
    </xsl:for-each>
    </svg:svg>
  </xsl:template>	

  
  <xsl:template match="svg:svg">
    <xsl:apply-templates select="node()"/>
  </xsl:template>

  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
  </xsl:template>
  

</xsl:stylesheet>
