/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.trino.tpcds.row;

import io.trino.tpcds.generator.GeneratorColumn;

import static io.trino.tpcds.type.Date.fromJulianDays;

public abstract class TableRowWithNulls
        implements TableRow
{
    public volatile long t1, t2, t3, t4, t5, t6, t7;
    private final long nullBitMap;

    public volatile long t8, t9, t10, t11, t12, t13, t14;
    private final GeneratorColumn firstColumn;

    protected TableRowWithNulls(long nullBitMap, GeneratorColumn firstColumn)
    {
        this.nullBitMap = nullBitMap;
        this.firstColumn = firstColumn;
    }

    public long getPadding() {
        return t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8 + t9 + t10 + t11 + t12 + t13 + t14;
    }

    private boolean isNull(GeneratorColumn column)
    {
        long kBitMask = 1L << (column.getGlobalColumnNumber() - firstColumn.getGlobalColumnNumber());
        return (nullBitMap & kBitMask) != 0;
    }

    protected <T> String getStringOrNull(T value, GeneratorColumn column)
    {
        return isNull(column) ? null : value.toString();
    }

    protected <T> String getStringOrNullForKey(long value, GeneratorColumn column)
    {
        return (isNull(column) || value == -1) ? null : Long.toString(value);
    }

    protected <T> String getStringOrNullForBoolean(boolean value, GeneratorColumn column)
    {
        if (isNull(column)) {
            return null;
        }

        return value ? "Y" : "N";
    }

    protected <T> String getDateStringOrNullFromJulianDays(long value, GeneratorColumn column)
    {
        return (isNull(column) || value < 0) ? null : fromJulianDays((int) value).toString();
    }
}
