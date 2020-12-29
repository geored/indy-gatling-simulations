
package io.swagger.client.model


case class RemoteRepository (
    /* Serialized store key, of the form: '[hosted|group|remote]:name' */
    _key: String,
    _description: Option[String],
    _metadata: Option[Map[String, String]],
    _disabled: Option[Boolean],
    _packageType: Option[String],
    _name: Option[String],
    _type: Option[String],
    /* Integer time in seconds which is used for repo automatically re-enable when set disable by errors, positive value means time in seconds, -1 means never disable, empty or 0 means use default timeout. */
    _disable_timeout: Option[Integer],
    _path_style: Option[String],
    _path_mask_patterns: Option[List[String]],
    _authoritative_index: Option[Boolean],
    _create_time: Option[String],
    _host: Option[String],
    _port: Option[Integer],
    _user: Option[String],
    _password: Option[String],
    _allow_snapshots: Option[Boolean],
    _allow_releases: Option[Boolean],
    /* The remote URL to proxy */
    _url: String,
    _timeout_seconds: Option[Integer],
    _max_connections: Option[Integer],
    _ignore_hostname_verification: Option[Boolean],
    _nfc_timeout_seconds: Option[Integer],
    _is_passthrough: Option[Boolean],
    _cache_timeout_seconds: Option[Integer],
    _metadata_timeout_seconds: Option[Integer],
    _key_password: Option[String],
    _key_certificate_pem: Option[String],
    _server_certificate_pem: Option[String],
    _proxy_host: Option[String],
    _proxy_port: Option[Integer],
    _proxy_user: Option[String],
    _proxy_password: Option[String],
    _server_trust_policy: Option[String],
    /* Integer to indicate the pre-fetching priority of the remote, higher means more eager to do the pre-fetching of the content in the repo, 0 or below means disable the pre-fecthing. */
    _prefetch_priority: Option[Integer],
    /* Indicates if the remote needs to do rescan after prefetch */
    _prefetch_rescan: Option[Boolean],
    /* The prefetch listing type, should be html or koji */
    _prefetch_listing_type: Option[String],
    _prefetch_rescan_time: Option[String]
)
object RemoteRepository {
    def toStringBody(var_key: Object, var_description: Object, var_metadata: Object, var_disabled: Object, var_packageType: Object, var_name: Object, var_type: Object, var_disable_timeout: Object, var_path_style: Object, var_path_mask_patterns: Object, var_authoritative_index: Object, var_create_time: Object, var_host: Object, var_port: Object, var_user: Object, var_password: Object, var_allow_snapshots: Object, var_allow_releases: Object, var_url: Object, var_timeout_seconds: Object, var_max_connections: Object, var_ignore_hostname_verification: Object, var_nfc_timeout_seconds: Object, var_is_passthrough: Object, var_cache_timeout_seconds: Object, var_metadata_timeout_seconds: Object, var_key_password: Object, var_key_certificate_pem: Object, var_server_certificate_pem: Object, var_proxy_host: Object, var_proxy_port: Object, var_proxy_user: Object, var_proxy_password: Object, var_server_trust_policy: Object, var_prefetch_priority: Object, var_prefetch_rescan: Object, var_prefetch_listing_type: Object, var_prefetch_rescan_time: Object) =
        s"""
        | {
        | "key":$var_key,"description":$var_description,"metadata":$var_metadata,"disabled":$var_disabled,"packageType":$var_packageType,"name":$var_name,"type":$var_type,"disable_timeout":$var_disable_timeout,"path_style":$var_path_style,"path_mask_patterns":$var_path_mask_patterns,"authoritative_index":$var_authoritative_index,"create_time":$var_create_time,"host":$var_host,"port":$var_port,"user":$var_user,"password":$var_password,"allow_snapshots":$var_allow_snapshots,"allow_releases":$var_allow_releases,"url":$var_url,"timeout_seconds":$var_timeout_seconds,"max_connections":$var_max_connections,"ignore_hostname_verification":$var_ignore_hostname_verification,"nfc_timeout_seconds":$var_nfc_timeout_seconds,"is_passthrough":$var_is_passthrough,"cache_timeout_seconds":$var_cache_timeout_seconds,"metadata_timeout_seconds":$var_metadata_timeout_seconds,"key_password":$var_key_password,"key_certificate_pem":$var_key_certificate_pem,"server_certificate_pem":$var_server_certificate_pem,"proxy_host":$var_proxy_host,"proxy_port":$var_proxy_port,"proxy_user":$var_proxy_user,"proxy_password":$var_proxy_password,"server_trust_policy":$var_server_trust_policy,"prefetch_priority":$var_prefetch_priority,"prefetch_rescan":$var_prefetch_rescan,"prefetch_listing_type":$var_prefetch_listing_type,"prefetch_rescan_time":$var_prefetch_rescan_time
        | }
        """.stripMargin
}
