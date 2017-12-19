#/usr/bin/python
# 2016.04.29 版本1.0
# 2017.07.29 版本2.0
from urllib import request
from lxml import etree
from selenium import webdriver
import time

# 京东手机商品页面
url = "https://www.semanticscholar.org/search?q=Compactification%2C%20Geometry%20and%20Duality%3A%20N%3D2&sort=relevance"

# 下面的xslt是通过集搜客的谋数台图形界面自动生成的
xslt_root = etree.XML("""\
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:template match="/">
<li>
<xsl:apply-templates select="//*[@class='sku-name' and count(././text())>0]" mode="li"/>
</li>
</xsl:template>


<xsl:template match="//*[@class='sku-name' and count(././text())>0]" mode="li">
<item>
<商品名称>
<xsl:value-of select="./text()"/>
</商品名称>
<京东价>
<xsl:value-of select="following-sibling::div[position()=2]//*[@class='p-price']/span[position()=2]/text()"/>
</京东价>
</item>
</xsl:template>
</xsl:stylesheet>""")

# 使用webdriver.PhantomJS
browser = webdriver.PhantomJS(executable_path='/Users/alex/UChicago/phantomjs-2.1.1-macosx/bin/phantomjs')
browser.get(url)
time.sleep(2)

transform = etree.XSLT(xslt_root)

# 执行js得到整个dom
html = browser.execute_script("return document.documentElement.outerHTML")
doc = etree.HTML(html)
# 用xslt从dom中提取需要的字段
#result_tree = transform(doc)
print(html.encode('utf-8'))