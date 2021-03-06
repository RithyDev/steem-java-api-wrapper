/*
 *     This file is part of SteemJ (formerly known as 'Steem-Java-Api-Wrapper')
 * 
 *     SteemJ is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SteemJ is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SteemJ.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.bittrade.libs.steemj.plugins.apis.market.history.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.fc.TimePointSec;
import eu.bittrade.libs.steemj.protocol.LegacyAsset;

/**
 * This class represents a Steem "market_trade" object of the
 * "market_history_plugin".
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class MarketTrade {
    @JsonProperty("date")
    private TimePointSec date;
    @JsonProperty("current_pays")
    private LegacyAsset currentPays;
    @JsonProperty("open_pays")
    private LegacyAsset openPays;

    /**
     * This object is only used to wrap the JSON response in a POJO, so
     * therefore this class should not be instantiated.
     */
    protected MarketTrade() {
    }

    /**
     * @return the date
     */
    public TimePointSec getDate() {
        return date;
    }

    /**
     * @return the currentPays
     */
    public LegacyAsset getCurrentPays() {
        return currentPays;
    }

    /**
     * @return the openPays
     */
    public LegacyAsset getOpenPays() {
        return openPays;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
