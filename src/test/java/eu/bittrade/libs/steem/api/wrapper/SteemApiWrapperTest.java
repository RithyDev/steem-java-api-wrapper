package eu.bittrade.libs.steem.api.wrapper;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.exparity.hamcrest.date.DateMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.bittrade.libs.steem.api.wrapper.exceptions.SteemResponseError;
import eu.bittrade.libs.steem.api.wrapper.models.AccountActivity;
import eu.bittrade.libs.steem.api.wrapper.models.ActiveVote;
import eu.bittrade.libs.steem.api.wrapper.models.Asset;
import eu.bittrade.libs.steem.api.wrapper.models.ChainProperties;
import eu.bittrade.libs.steem.api.wrapper.models.Config;
import eu.bittrade.libs.steem.api.wrapper.models.Content;
import eu.bittrade.libs.steem.api.wrapper.models.GlobalProperties;
import eu.bittrade.libs.steem.api.wrapper.models.LiquidityQueueEntry;
import eu.bittrade.libs.steem.api.wrapper.models.Transaction;
import eu.bittrade.libs.steem.api.wrapper.models.TrendingTag;
import eu.bittrade.libs.steem.api.wrapper.models.Version;
import eu.bittrade.libs.steem.api.wrapper.models.Vote;
import eu.bittrade.libs.steem.api.wrapper.models.Witness;
import eu.bittrade.libs.steem.api.wrapper.models.WitnessSchedule;
import eu.bittrade.libs.steem.api.wrapper.models.operations.AccountCreateOperation;
import eu.bittrade.libs.steem.api.wrapper.models.operations.Operation;
import eu.bittrade.libs.steem.api.wrapper.models.operations.VoteOperation;
import eu.bittrade.libs.steem.api.wrapper.util.DiscussionSortType;

/**
 * @author Anthony Martin
 */
public class SteemApiWrapperTest extends BaseTest {
    private static final Logger LOGGER = LogManager.getLogger(SteemApiWrapperTest.class);
    private static final String ACCOUNT = "dez1337";
    private static final String WITNESS_ACCOUNT = "good-karma";
    private static final String PERMLINK = "steem-api-wrapper-for-java-update1";
    private static final String WIF = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";
    private static final int REF_BLOCK_NUM = 34294;
    private static final long REF_BLOCK_PREFIX = 3707022213L;
    private static final String EXPIRATION_DATE = "2016-04-06T08:29:27UTC";

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void getApiByName() throws Exception {
        final String bogus = steemApiWrapper.getApiByName("bogus_api");
        final String database = steemApiWrapper.getApiByName("database_api");
        final String login = steemApiWrapper.getApiByName("login_api");
        final String market_history = steemApiWrapper.getApiByName("market_history_api");
        final String follow = steemApiWrapper.getApiByName("follow_api");

        assertNull("expect that bogus api does not exist", bogus);
        assertNotNull("expect that database api does exist", database);
        assertNotNull("expect that login api does exist", login);
        assertNotNull("expect that market_history api does exist", market_history);
        assertNotNull("expect that follow api does exist", follow);
    }

    @Category(PrivateNode.class)
    @Test
    public void getApiByNameForSecuredApi() throws Exception {
        final String tags = steemApiWrapper.getApiByName("tags_api");
        final String network_node = steemApiWrapper.getApiByName("network_node_api");
        final String network_broadcast = steemApiWrapper.getApiByName("network_broadcast_api");
        final String chain_stats = steemApiWrapper.getApiByName("chain_stats_api");

        assertNotNull("expect that tags api does exist", tags);
        assertNotNull("expect that network_node api does exist", network_node);
        assertNotNull("expect that network_broadcast api does exist", network_broadcast);
        assertNotNull("expect that chain_stats api does exist", chain_stats);
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testAccountCount() throws Exception {
        final int accountCount = steemApiWrapper.getAccountCount();

        assertThat("expect the number of accounts greater than 122908", accountCount, greaterThan(122908));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testAccountHistory() throws Exception {
        final Map<Integer, AccountActivity> accountHistory = steemApiWrapper.getAccountHistory(ACCOUNT, 10, 10);
        assertEquals("expect response to contain 10 results", 11, accountHistory.size());

        Operation firstOperation = accountHistory.get(0).getOperations();
        assertTrue("the first operation for each account is the 'account_create_operation'",
                firstOperation instanceof AccountCreateOperation);
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testActiveVotes() throws Exception {
        // Get the votes done by the specified account:
        final List<Vote> votes = steemApiWrapper.getAccountVotes(ACCOUNT);
        final List<ActiveVote> activeVotesForArticle = steemApiWrapper.getActiveVotes(ACCOUNT, PERMLINK);

        assertNotNull("expect votes", votes);
        assertThat("expect account has votes", votes.size(), greaterThan(0));
        assertThat("expect last vote after 2016-03-01", votes.get(votes.size() - 1).getTime(),
                DateMatchers.after(2016, Month.MARCH, 1));

        boolean foundSelfVote = false;

        for (final ActiveVote vote : activeVotesForArticle) {
            if (ACCOUNT.equals(vote.getVoter())) {
                foundSelfVote = true;
                break;
            }
        }

        assertTrue("expect self vote for article of account", foundSelfVote);
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Ignore("not fully implemented")
    @Test
    public void testBroadcastTransactionSynchronous() throws Exception {
        final boolean success = steemApiWrapper.broadcastTransactionSynchronous("TODO");

        assertTrue("expect broadcast success: true", success);
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testConfig() throws Exception {
        final Config config = steemApiWrapper.getConfig();
        final boolean isTestNet = config.getIsTestNet();
        final String steemitNullAccount = config.getSteemitNullAccount();
        final String initMinerName = config.getSteemitInitMinerName();

        assertEquals("expect main net", false, isTestNet);
        assertEquals("expect the null account to be null", "null", steemitNullAccount);
        assertEquals("expect the init miner name to be initminer", "initminer", initMinerName);
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testCurrentMedianHistoryPrice() throws Exception {
        final Asset base = steemApiWrapper.getCurrentMedianHistoryPrice().getBase();
        final Asset quote = steemApiWrapper.getCurrentMedianHistoryPrice().getQuote();

        assertThat("expect current median price greater than zero", base.getAmount(), greaterThan(0.00));
        assertEquals("expect current median price symbol", "SBD", base.getSymbol());
        assertThat("expect current median price greater than zero", quote.getAmount(), greaterThan(0.00));
        assertEquals("expect current median price symbol", "STEEM", quote.getSymbol());
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetActiveWitnesses() throws Exception {
        final String[] activeWitnesses = steemApiWrapper.getActiveWitnesses();

        assertTrue("expect " + WITNESS_ACCOUNT + " to be an active witness.",
                Arrays.asList(activeWitnesses).contains(WITNESS_ACCOUNT));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetChainProperties() throws Exception {
        final ChainProperties properties = steemApiWrapper.getChainProperties();

        assertNotNull("expect properties", properties);
        assertThat("expect sbd interest rate", properties.getSdbInterestRate(), greaterThan(0));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetContent() throws Exception {
        final Content discussion = steemApiWrapper.getContent(ACCOUNT, PERMLINK);

        assertNotNull("expect discussion", discussion);
        assertEquals("expect correct author", ACCOUNT, discussion.getAuthor());
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetContentReplies() throws Exception {
        final List<Content> replies = steemApiWrapper.getContentReplies(ACCOUNT, PERMLINK);

        assertNotNull("expect replies", replies);
        assertThat("expect replies greater than zero", replies.size(), greaterThan(0));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetDiscussionBy() throws Exception {

        final DiscussionSortType[] activeTypes = new DiscussionSortType[] { DiscussionSortType.SORT_BY_TRENDING,
                DiscussionSortType.SORT_BY_TRENDING_30_DAYS, DiscussionSortType.SORT_BY_CREATED,
                DiscussionSortType.SORT_BY_ACTIVE, DiscussionSortType.SORT_BY_CASHOUT, DiscussionSortType.SORT_BY_VOTES,
                DiscussionSortType.SORT_BY_CHILDREN, DiscussionSortType.SORT_BY_HOT, DiscussionSortType.SORT_BY_BLOG,
                DiscussionSortType.SORT_BY_PROMOTED };

        final DiscussionSortType[] inactiveTypes = new DiscussionSortType[] { DiscussionSortType.SORT_BY_PAYOUT,
                DiscussionSortType.SORT_BY_FEED };

        final DiscussionSortType[] invalidTypes = new DiscussionSortType[] { DiscussionSortType.SORT_BY_COMMENTS };

        for (final DiscussionSortType type : activeTypes) {
            final List<Content> discussions = steemApiWrapper.getDiscussionsBy("steemit", 1, type);
            assertNotNull("expect discussions", discussions);
            assertThat("expect discussions in " + type + " greater than zero", discussions.size(), greaterThan(0));
        }

        for (final DiscussionSortType type : inactiveTypes) {
            final List<Content> discussions = steemApiWrapper.getDiscussionsBy("steemit", 1, type);
            assertNotNull("expect discussions", discussions);
            assertEquals("expect discussions in " + type + " of zero", 0, discussions.size());
        }

        for (final DiscussionSortType type : invalidTypes) {
            try {
                steemApiWrapper.getDiscussionsBy("steemit", 1, type);
                fail("did not expect discussions in " + type + " to return valid");
            } catch (SteemResponseError e) {
                // success
            }
        }
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetDiscussionsByAuthorBeforeDate() throws Exception {
        final List<Content> repliesByLastUpdate = steemApiWrapper.getDiscussionsByAuthorBeforeDate(ACCOUNT, PERMLINK,
                "2017-02-10T22:00:06", 8);

        assertEquals("expect that 8 results are returned", repliesByLastUpdate.size(), 8);
        assertEquals("expect " + ACCOUNT + " to be the first returned author", repliesByLastUpdate.get(0).getAuthor(),
                ACCOUNT);
        assertEquals("expect " + PERMLINK + " to be the first returned permlink", PERMLINK,
                repliesByLastUpdate.get(0).getPermlink());
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetDynamicGlobalProperties() throws Exception {
        final GlobalProperties properties = steemApiWrapper.getDynamicGlobalProperties();

        assertNotNull("expect properties", properties);
        assertThat("expect head block number", properties.getHeadBlockNumber(), greaterThan(6000000));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetLiquidityQueue() throws Exception {
        final List<LiquidityQueueEntry> repliesByLastUpdate = steemApiWrapper.getLiquidityQueue(WITNESS_ACCOUNT, 5);

        assertEquals("expect that 5 results are returned", repliesByLastUpdate.size(), 5);
        assertEquals("expect " + WITNESS_ACCOUNT + " to be the first returned account", WITNESS_ACCOUNT,
                repliesByLastUpdate.get(0).getAccount());
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetRepliesByLastUpdate() throws Exception {
        final List<Content> repliesByLastUpdate = steemApiWrapper.getRepliesByLastUpdate(ACCOUNT, PERMLINK, 9);

        assertEquals("expect that 9 results are returned", repliesByLastUpdate.size(), 9);
        assertEquals("expect " + ACCOUNT + " to be the first returned author", ACCOUNT,
                repliesByLastUpdate.get(0).getAuthor());
    }

    @Category({ PrivateNode.class })
    @Test
    public void testGetTransactionHex() throws Exception {
        final String EXPECTED_RESULT = "f68585abf4dce7c80457010007666f6f6261726107666f6f62617263"
                + "07666f6f62617264e8030001202e09123f732a438ef6d6138484d7ad"
                + "edfdcf4a4f3d171f7fcafe836efa2a3c8877290bd34c67eded824ac0"
                + "cc39e33d154d0617f64af936a83c442f62aef08fec";

        VoteOperation voteOperation = new VoteOperation();
        voteOperation.setAuthor("foobarc");
        voteOperation.setPermlink("foobard");
        voteOperation.setVoter("foobara");
        voteOperation.setWeight((short) 1000);

        Operation[] operations = { voteOperation };

        Transaction transaction = new Transaction();
        transaction.setExpirationDate(EXPIRATION_DATE);
        transaction.setRefBlockNum(REF_BLOCK_NUM);
        transaction.setRefBlockPrefix(REF_BLOCK_PREFIX);
        transaction.setOperations(operations);

        List<ECKey> wifKeys = new ArrayList<>();
        ECKey privateKey = DumpedPrivateKey.fromBase58(null, WIF).getKey();
        wifKeys.add(privateKey);

        transaction.sign(wifKeys);

        assertEquals("expect the correct hex value", EXPECTED_RESULT, steemApiWrapper.getTransactionHex(transaction));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetWitnessByAccount() throws Exception {
        final Witness activeWitnessesByVote = steemApiWrapper.getWitnessByAccount(WITNESS_ACCOUNT);

        assertEquals("expect " + WITNESS_ACCOUNT + " to be the owner of the returned witness account", WITNESS_ACCOUNT,
                activeWitnessesByVote.getOwner());
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testGetWitnessesByVote() throws Exception {
        final List<Witness> activeWitnessesByVote = steemApiWrapper.getWitnessByVote(WITNESS_ACCOUNT, 10);

        assertEquals("expect that 10 results are returned", activeWitnessesByVote.size(), 10);
        assertEquals("expect " + WITNESS_ACCOUNT + " to be the first returned witness", WITNESS_ACCOUNT,
                activeWitnessesByVote.get(0).getOwner());
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testHardforkVersion() throws Exception {
        final String hardforkVersion = steemApiWrapper.getHardforkVersion();

        assertNotNull("expect hardfork version", hardforkVersion);
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testInvalidAccountVotes() throws Exception {
        // Force an error response:
        try {
            steemApiWrapper.getAccountVotes("thisAcountDoesNotExistYet");
        } catch (final SteemResponseError steemResponseError) {
            // success
        } catch (final Exception e) {
            LOGGER.error(e);
            fail(e.toString());
        }
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testLogin() throws Exception {
        final boolean success = steemApiWrapper.login("gilligan", "s.s.minnow");

        assertTrue("expect login to always return success: true", success);
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testLookupAccount() throws Exception {
        final List<String> accounts = steemApiWrapper.lookupAccounts(ACCOUNT, 10);

        assertNotNull("expect accounts", accounts);
        assertThat("expect at least one account", accounts.size(), greaterThan(0));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testLookupWitnessAccount() throws Exception {
        final List<String> accounts = steemApiWrapper.lookupWitnessAccounts("gtg", 10);

        assertNotNull("expect accounts", accounts);
        assertThat("expect at least one account", accounts.size(), greaterThan(0));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testMinerQueue() throws Exception {
        final String[] minerQueue = steemApiWrapper.getMinerQueue();

        assertThat("expect the number of miners greater than 0", minerQueue.length, greaterThan(0));
    }

    @Category({ PrivateNode.class })
    @Ignore("not fully implemented")
    @Test
    public void testNodeInfo() throws Exception {
        // final NodeInfo nodeInfo = steemApiWrapper.getNodeInfo();
        // TODO write assertions
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testTrendingTags() throws Exception {
        final List<TrendingTag> trendingTags = steemApiWrapper.getTrendingTags(null, 10);

        assertNotNull("expect trending tags", trendingTags);
        assertThat("expect trending tags size > 0", trendingTags.size(), greaterThan(0));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testVersion() throws Exception {
        final Version version = steemApiWrapper.getVersion();

        assertNotEquals("expect non-empty blockchain version", "", version.getBlockchainVersion());
        assertNotEquals("expect non-empty fc revision", "", version.getFcRevision());
        assertNotEquals("expect non-empty steem revision", "", version.getSteemRevision());
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testWitnessCount() throws Exception {
        final int witnessCount = steemApiWrapper.getWitnessCount();

        assertThat("expect the number of witnesses greater than 13071", witnessCount, greaterThan(13071));
    }

    @Category({ PublicNode.class, PrivateNode.class })
    @Test
    public void testWitnessSchedule() throws Exception {
        final WitnessSchedule witnessSchedule = steemApiWrapper.getWitnessSchedule();

        assertNotNull("expect hardfork version", witnessSchedule);
    }
}