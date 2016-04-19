package in.arnavgupta.badootransactionapp.utils;

/**
 * Created by championswimmer on 19/4/16.
 */
import android.util.Log;

import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.*;

/**
 * This class implements the a currency converter.
 * Because not every exchange rate is directly defined, the basis data structure is an
 * unweighted, directed graph, with strings as vertices and edges holding the exchange rate.
 *    SimpleDirectedGraph<String, Double>
 * Final exchange rates are computed using Dijkstra's shortest path algorithm.
 *
 * a) In realistic cases, paths between currencies should be relatively small (max 2,3 edges),
 * so that no major performance issues should exist (application dependent).
 * If that is the case, a possibility straightforward improvement could be made by
 * using a cache structure, e.g of type TreeMap< String, TreeMap< String, Double> >, where all
 * possible exchange rates are precomputed and stored.
 * It would be updated every time the graph is changed.
 *
 * b) The class is not yet thread-safe. As soon as a concrete application where that is an issue,
 * the issue will be addressed.
 *
 * c) Vertices are String because of the initial definition of the problem. Ideally, however, one
 * could use java.util.Currency, which implements ISO-4217
 *
 *
 * @author Miguel Vaz
 *
 */

public class CurrencyConverter {

    public static final String TAG = "CurCov";

    /**
     * In a change from Miguel's example code, I am using a wrapper class for edges,
     * because each edge must by unique.
     * A graph expects the 'names' of it's edges to be unique. If we use a raw Double,
     * then the value of the double is it's 'name', in which case, if two sets of
     * currencies have same ratio, then the Graph won't accept that edge, as the name
     * will not be unique. So I am wrapping it into a edge {String name, Double rate}
     * form.
     */
    class CurRate {
        public CurRate(Double rate, String edgeName) {
            this.rate = rate;
            EdgeName = edgeName;
        }

        public Double rate;
        public String EdgeName;
    }

    /*
     * directed, because the exchange rate is direction specific
     * unweighted, because the exchange rates are the edges themselves
     */
    private SimpleDirectedGraph<String,CurRate> currencyGraph;

    /**
     * Simple constructor. Initializes nothing.
     */
    public CurrencyConverter() {
        currencyGraph = new SimpleDirectedGraph<String, CurRate>(CurRate.class);
    }

    /**
     * Defines (or changes) the exchange rate between the origin and goal currencies.
     * TODO: not thread-safe
     *
     * @param origin
     * @param goal
     * @param rate
     * @return
     */
    public boolean setExchangeRate(String origin, String goal, double rate)
    {
        // add the vertices (currencies) if they do not exist
        boolean originAdded = currencyGraph.addVertex(origin);
        boolean goalAdded = currencyGraph.addVertex(goal);


        // check whether the edge already exists
        // if so, remove it, in order to add it
        if (currencyGraph.containsEdge(origin, goal)) {
            currencyGraph.removeEdge(origin, goal);
            currencyGraph.removeEdge(goal, origin);
        }
        // add the edge
        boolean	addDirectCurrency = currencyGraph.addEdge(origin, goal, new CurRate(rate, origin+goal));
        // and the direct inverse edge, with the inverse weight
        boolean	addReverseCurrency = currencyGraph.addEdge(goal, origin, new CurRate((1.0/rate), goal+origin));



        return addDirectCurrency && addReverseCurrency;
    }

    /**
     * Converts a given amount of an origin currency to a goal currency.
     * TODO: not thread-safe
     *
     * @param origin the string identifier of the currency to be exchanged
     * @param goal the string identifier of the target currency
     * @param amount the amount of the origin currency to be exchanged
     * @return the amount of converted currency
     * @throws ArithmeticException
     */
    public double convertCurrency( String origin, String goal, double amount ) throws ArithmeticException
    {
        // find the shortest path between the two currencies

        List<CurRate> l = DijkstraShortestPath.findPathBetween(currencyGraph, origin, goal);

        // when there is no path between the 2 nodes / vertices / currencies
        // DijkstraShortestPath returns null
        if ( l == null)
            throw new ArithmeticException("This exchange does not exist" + origin + " to " + goal);

        // navigate the edges and iteratively compute the exchange rate
        double rate = 1.0;
        for (CurRate edge : l) {
            rate = rate * edge.rate;
        }

        // compute and return the currency value
        return amount * rate;
    }

    /**
     * Checks whether the currency is already represented.
     *
     * @param currency
     * @return
     */
    public boolean containsCurrency( String currency )
    {
        return currencyGraph.containsVertex(currency);
    }

    @Override
    public String toString()
    {
        return currencyGraph.toString();
    }
}
