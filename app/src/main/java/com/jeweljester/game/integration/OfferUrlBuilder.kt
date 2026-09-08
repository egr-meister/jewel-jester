package com.jeweljester.game.integration

/**
 * Assembles the offer URL from the base URL and everything AppsFlyer reported.
 *
 * Built by string concatenation on purpose: android.net.Uri.Builder percent-encodes
 * values that are already encoded, so a campaign carrying `%26` would reach the tracker
 * as `%2526`.
 *
 * `sub1` (parsed out of `campaign`) is placed as a path segment right after the base
 * path AND kept in the query — the offer server routes on the path segment, while the
 * tracker still reads sub1 from the query.
 */
object OfferUrlBuilder {

    private const val CAMPAIGN_KEY = "campaign"
    private const val APPSFLYER_ID_KEY = "appsflyer_id"
    private const val SUB1_KEY = "sub1"

    fun build(
        baseUrl: String,
        appsFlyerParams: Map<String, String>,
        appsFlyerId: String?
    ): String {
        val base = baseUrl.trim()
        if (base.isEmpty()) return ""

        // Split without decoding - anything already encoded must pass through as-is.
        val hashIndex = base.indexOf('#')
        val fragment = if (hashIndex >= 0) base.substring(hashIndex) else ""
        val withoutFragment = if (hashIndex >= 0) base.substring(0, hashIndex) else base

        val questionIndex = withoutFragment.indexOf('?')
        val path = if (questionIndex >= 0) withoutFragment.substring(0, questionIndex) else withoutFragment
        val baseQuery = if (questionIndex >= 0) withoutFragment.substring(questionIndex + 1) else ""

        val params = LinkedHashMap<String, String>()
        parseQuery(baseQuery, params)
        // Every key AppsFlyer sent, in arrival order. Filtering to a known subset here is
        // the single most common way this integration silently loses attribution.
        params.putAll(appsFlyerParams)

        val campaign = appsFlyerParams[CAMPAIGN_KEY].orEmpty()
        val subs = CampaignParser.parse(campaign)

        // The tracker reads the raw campaign as well as the exploded parts, so it stays.
        if (campaign.isNotEmpty()) params[CAMPAIGN_KEY] = campaign

        // Parsed subs win over any sub1..subN AppsFlyer happened to send itself.
        // Re-putting an existing key replaces the value in place, so ordering stays
        // deterministic and no query key is emitted twice.
        params.putAll(subs)

        // A blank id would reach the tracker as an empty appsflyer_id, which reads as
        // "attributed to nothing" rather than "not known yet".
        if (!appsFlyerId.isNullOrBlank()) params[APPSFLYER_ID_KEY] = appsFlyerId.trim()

        val query = CampaignParser.toQuery(params)

        // sub1 goes into the path as well; empty/absent sub1 leaves the path unchanged.
        val sub1 = subs[SUB1_KEY].orEmpty()
        val finalPath = if (sub1.isNotEmpty()) appendPathSegment(path, sub1) else path

        return buildString {
            append(finalPath)
            if (query.isNotEmpty()) {
                append('?')
                append(query)
            }
            append(fragment)
        }
    }

    /**
     * Appends a single encoded segment after the existing path. Idempotent: if the path
     * already ends with this segment (build() called twice on its own output), it is not
     * added again.
     */
    private fun appendPathSegment(path: String, segment: String): String {
        val encoded = PercentEncoder.encodePathSegment(segment)
        val trimmed = path.trimEnd('/')
        val lastSegment = trimmed.substringAfterLast('/', "")
        return if (lastSegment == encoded) path else "$trimmed/$encoded"
    }

    private fun parseQuery(query: String, into: LinkedHashMap<String, String>) {
        if (query.isEmpty()) return
        for (pair in query.split('&')) {
            if (pair.isEmpty()) continue
            val eq = pair.indexOf('=')
            if (eq < 0) into[pair] = "" else into[pair.substring(0, eq)] = pair.substring(eq + 1)
        }
    }
}
