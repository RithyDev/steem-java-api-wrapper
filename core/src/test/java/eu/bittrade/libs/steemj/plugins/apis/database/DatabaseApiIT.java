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
package eu.bittrade.libs.steemj.plugins.apis.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hamcrest.Matcher;
import org.joou.UInteger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.bittrade.libs.steemj.BaseIT;
import eu.bittrade.libs.steemj.IntegrationTest;
import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.account.history.AccountHistoryApi;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.AppliedOperation;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockArgs;
import eu.bittrade.libs.steemj.plugins.apis.database.models.DynamicGlobalProperty;
import eu.bittrade.libs.steemj.plugins.apis.database.models.HardforkProperty;
import eu.bittrade.libs.steemj.plugins.apis.tags.TagsApi;
import eu.bittrade.libs.steemj.plugins.apis.tags.models.Tag;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.enums.LegacyAssetSymbolType;
import eu.bittrade.libs.steemj.protocol.operations.CommentOperation;
import eu.bittrade.libs.steemj.protocol.operations.Operation;

/**
 * This class contains all test connected to the
 * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi
 * DatabaseApi}.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class DatabaseApiIT extends BaseIT {
    private static CommunicationHandler COMMUNICATION_HANDLER;

    /**
     * Setup the test environment.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     */
    @BeforeClass
    public static void init() throws SteemCommunicationException {
        setupIntegrationTestEnvironment();

        COMMUNICATION_HANDLER = new CommunicationHandler();
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getHardforkProperties(CommunicationHandler)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetHardforkProperties() throws SteemCommunicationException, SteemResponseException {
        HardforkProperty hardforkProperty = DatabaseApi.getHardforkProperties(COMMUNICATION_HANDLER);
        assertThat(hardforkProperty.getCurrentHardforkVersion().toString(), matchesPattern("^[0-9]+\\.[0-9]+\\.[0-9]+"));
        assertThat(hardforkProperty.getId(), greaterThanOrEqualTo(0L));
        assertThat(hardforkProperty.getLastHardfork().longValue(), greaterThanOrEqualTo(0L));
        assertThat(hardforkProperty.getNextHardfork().toString(), matchesPattern("^[0-9]+\\.[0-9]+\\.[0-9]+"));
        assertThat(hardforkProperty.getNextHardforkTime().getDateTimeAsTimestamp(), greaterThanOrEqualTo(0L));
        assertThat(hardforkProperty.getProcessedHardforks().size(), greaterThan(19));
        assertThat(hardforkProperty.getProcessedHardforks().get(0).getDateTimeAsTimestamp(), equalTo(1458835200000L));
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getDynamicGlobalProperties(CommunicationHandler)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetDynamicGlobalProperties() throws SteemCommunicationException, SteemResponseException {
        DynamicGlobalProperty dynamicGlobalProperty = DatabaseApi.getDynamicGlobalProperties(COMMUNICATION_HANDLER);

        // TODO: Test all fields.
        assertThat(dynamicGlobalProperty.getCurrentSdbSupply().getAmount(), greaterThan(10000L));
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getTrendingTags(CommunicationHandler, String, int)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetTrendingTags() throws SteemCommunicationException, SteemResponseException {
        final String REQUESTED_TAG = "steemit";

        final List<Tag> trendingTags = TagsApi.getTrendingTags(COMMUNICATION_HANDLER, REQUESTED_TAG, 2);

        assertNotNull(trendingTags);
        assertThat(trendingTags.size(), greaterThan(0));
        assertTrue(trendingTags.get(0).getName().equals(REQUESTED_TAG));
      //  assertThat(trendingTags.get(0).getComments(), greaterThan(0L));
      //  assertThat(trendingTags.get(0).getNetVotes(), greaterThan(0L));
       // assertThat(trendingTags.get(0).getTopPosts(), greaterThan(0L));
        // seems that payout asset report has changed
        // assertThat(trendingTags.get(0).getTotalPayouts().getSymbol(),
        // equalTo(AssetSymbolType.VESTS));
        assertThat(trendingTags.get(0).getTotalPayouts().getSymbol(), equalTo(LegacyAssetSymbolType.SBD));
        assertThat(trendingTags.get(0).getTotalPayouts().getAmount(), greaterThan(0L));
        assertThat(trendingTags.get(0).getTrending().intValue(), greaterThan(0));
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getState(CommunicationHandler, eu.bittrade.libs.steemj.base.models.Permlink)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetState() throws SteemCommunicationException, SteemResponseException {
        // TODO: Implement.
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getActiveWitnesses(CommunicationHandler)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetActiveWitnesses() throws SteemCommunicationException, SteemResponseException {
        final List<AccountName> activeWitnesses = DatabaseApi.getActiveWitnesses(COMMUNICATION_HANDLER);

        // The active witness changes from time to time, so we just check if
        // something is returned.
        assertThat(activeWitnesses.size(), greaterThan(0));
        assertThat(activeWitnesses.get(0).getName(), not(emptyOrNullString()));
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getOpsInBlock(CommunicationHandler, long, boolean)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetOpsInBlock() throws SteemCommunicationException, SteemResponseException {
          final List<AppliedOperation> appliedOperationsOnlyVirtual =
          AccountHistoryApi.getOpsInBlock(COMMUNICATION_HANDLER,new GetOpsInBlockArgs(UInteger.valueOf(5443322), true)).getOperations();
        //  LOGGER.debug(appliedOperationsOnlyVirtual.size());
          assertThat(appliedOperationsOnlyVirtual.get(0).getOpInTrx().intValue(),equalTo(0));
          /*assertThat(appliedOperationsOnlyVirtual.size(), equalTo(6));
          assertThat(appliedOperationsOnlyVirtual.get(0).getOpInTrx(),
          equalTo(1));
          assertThat(appliedOperationsOnlyVirtual.get(0).getTrxInBlock(),
          equalTo(41));
          assertThat(appliedOperationsOnlyVirtual.get(0).getVirtualOp(),
          equalTo(0L)); assertThat(appliedOperationsOnlyVirtual.get(0).getOp(),
          instanceOf(ProducerRewardOperation.class));*/
          
          final List<AppliedOperation> appliedOperations =
          AccountHistoryApi.getOpsInBlock(COMMUNICATION_HANDLER,new GetOpsInBlockArgs(UInteger.valueOf(1), false)).getOperations();
          assertThat(appliedOperations.get(0).getOpInTrx().intValue(),equalTo(0));
          /*assertThat(appliedOperations.size(), equalTo(51));
          assertThat(appliedOperations.get(1).getOpInTrx(), equalTo(0));
          assertThat(appliedOperations.get(1).getTrxInBlock(), equalTo(1));
          assertThat(appliedOperations.get(1).getVirtualOp(), equalTo(0L));
          assertThat(appliedOperations.get(1).getOp(), instanceOf(CommentOperation.class));*/
         
    }
}