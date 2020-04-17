/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.pagehelper.dialect.rowbounds;

import com.github.pagehelper.dialect.AbstractRowBoundsDialect;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.session.RowBounds;

/**
 * oracle 基于 RowBounds 的分页
 *
 * @author liuzh
 */
public class OracleRowBoundsDialect extends AbstractRowBoundsDialect {

    @Override
    public String getPageSql(String sql, RowBounds rowBounds, CacheKey pageKey) {
        int startRow = rowBounds.getOffset();
        int endRow = rowBounds.getOffset() + rowBounds.getLimit();
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        if (startRow > 0) {
            sqlBuilder.append("SELECT * FROM ( ");
        }
        if (endRow > 0) {
            sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM ( ");
        }
        sqlBuilder.append(sql);
        if (page.getEndRow() > 0 && page.getStartRow() > 0) {
            sqlBuilder.append(" ) TMP_PAGE ");
            sqlBuilder.append(" ) WHERE ROW_ID > ");
            sqlBuilder.append(page.getStartRow());
            pageKey.update(page.getStartRow());
            sqlBuilder.append(" AND ROW_ID <= ");
            sqlBuilder.append(page.getEndRow());
            pageKey.update(page.getEndRow());
        } else {
            if (page.getEndRow() > 0) {
                sqlBuilder.append(" ) TMP_PAGE WHERE ROWNUM <= ");
                sqlBuilder.append(page.getEndRow());
                pageKey.update(page.getEndRow());
            }
            if (page.getStartRow() > 0) {
                sqlBuilder.append(" ) WHERE ROW_ID > ");
                sqlBuilder.append(page.getStartRow());
                pageKey.update(page.getStartRow());
            }
        }
        return sqlBuilder.toString();
    }

}
