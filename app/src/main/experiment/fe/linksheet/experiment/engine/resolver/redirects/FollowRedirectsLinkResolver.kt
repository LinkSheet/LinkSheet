package fe.linksheet.experiment.engine.resolver.redirects

import fe.linksheet.experiment.engine.Cache
import fe.linksheet.experiment.engine.LinkResolver
import fe.linksheet.experiment.engine.ResolveInput
import fe.linksheet.experiment.engine.ResolveOutput
import org.koin.core.component.KoinComponent

class FollowRedirectsLinkResolver(
    private val source: FollowRedirectsLinkResolverSource
) : LinkResolver, KoinComponent {

    override suspend fun resolve(data: ResolveInput): ResolveOutput? {


        TODO()
    }

//    private suspend fun runRedirectResolver(
//        dispatcher: CoroutineDispatcher = Dispatchers.IO,
//        resolveModuleStatus: ResolveModuleStatus,
//        redirectResolver: RedirectUrlResolver,
//        uri: Uri,
//        canAccessInternet: Boolean = true,
//        requestTimeout: Int,
//        followRedirects: Boolean,
//        followRedirectsExternalService: Boolean,
//        followOnlyKnownTrackers: Boolean,
//        followRedirectsLocalCache: Boolean,
//        followRedirectsAllowDarknets: Boolean,
//    ): Uri? = withContext(dispatcher) {
//        logger.debug("Executing runRedirectResolver on ${Thread.currentThread().name}")
//
//        resolveModuleStatus.resolveIfEnabled(followRedirects, ResolveModule.Redirect, uri) { uriToResolve ->
//            logger.debug("Inside redirect func, on ${Thread.currentThread().name}")
//
//            val resolvePredicate: ResolvePredicate = { uri ->
//                (!followRedirectsExternalService && !followOnlyKnownTrackers) || FastForward.isTracker(uri.toString())
//            }
//
//            redirectResolver.resolve(
//                uriToResolve,
//                followRedirectsLocalCache,
//                resolvePredicate,
//                followRedirectsExternalService,
//                requestTimeout,
//                canAccessInternet,
//                followRedirectsAllowDarknets
//            )
//        }
//    }
}
