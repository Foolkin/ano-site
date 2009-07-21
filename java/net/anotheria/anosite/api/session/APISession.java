package net.anotheria.anosite.api.session;

import net.anotheria.util.TimeUnit;


public interface APISession {
	
	/**
	 * Default expiration period for attributes with policy autoexpire.
	 */
	public static final long DEFAULT_EXPIRE_PERIOD = TimeUnit.MINUTE.getMillis() * 5; // 5minutes
	
	/**
	 * This policy states that the attribute is for local use only and shouldn't be distributed.
	 */
	public int POLICY_LOCAL = 1;
	/**
	 * This policy states that the attribute will be distributed to other servers. Only useable with 
	 * Serializable attributes. <b>This feature isn't yet implemented</b>
	 */
	public int POLICY_DISTRIBUTED = 2;

	/**
	 * This policy states that the attribute will survice system or server restart. Only useable with 
	 * Serializable attributes. <b>This feature isn't yet implemented</b>
	 */
	public int POLICY_PERSISTENT = 4;
	/**
	 * If set this policy defines that after the specified expire period the attribute will be reseted and not visible 
	 * to the caller. The attribute itself will not be deleted until explicitely overwritten or removed, so don't use this
	 * policy on timer-bound attributes, since that can lead to unexpected behaviour. However, you shouldn't put timer-bound
	 * attributes in the session either way.
	 */
	public int POLICY_AUTOEXPIRE = 8;
	
	/**
	 * This policy ensures that instead of making a new AttributeWrapper, the old one will be reused (if available).
	 * This is important if you want to keep some wrapper attributes like autoexpire. If you put an attribute with an 
	 * autoexpire policy on but without the reuse_wrapper policy, the autoexpiring will be reset on each setAttribute call.
	 * If you want to keep a 'global' autoexpiring.
	 */
	public int POLICY_REUSE_WRAPPER = 16;
	
	/**
	 * This policy enables the attribute to survive the session clean-up on logout.
	 */
	public int POLICY_SURVIVE_LOGOUT = 32;

	/**
	 * Default attribute policy.
	 */
	public int POLICY_DEFAULT = POLICY_LOCAL;
	
	/**
	 * Sets the object in the session under the given name under the usage of the default policy.
	 * @param key the name of the attribute in the session
	 * @param value the object to store in the session.
	 */
	void setAttribute(String key, Object value);
	
	/**
	 * Sets the object as session attribute into the session under the given policy.
	 * @param key the name under which the attribute is stored.
	 * @param policy the policy to be applied to the attribute. See POLICY_ constants for details.
	 * @param value the value to store in the session.
	 */
	void setAttribute(String key, int policy, Object value);
	
	/**
	 * Adds an attribute with given key, policy, value and expiration time.
	 * @param key
	 * @param policy
	 * @param value
	 * @param expiresWhen
	 */
	void setAttribute(String key, int policy, Object value, long expiresWhen);

	/**
	 * Returns the session attribute with the given key.
	 * @param key the key under which the attribute is stored.
	 * @return the stored attribute or null if no attribute under that name is stored.
	 */
	Object getAttribute(String key);
	/**
	 * Removes the attribute with under the given key. If no such attribute is present the method returns without failing.
	 * @param key the attribute name(key) to remove
	 */
	void removeAttribute(String key);
	
	/**
	 * Returns the session id.
	 * @return
	 */
	String getId();
	/**
	 * Returns the ip adress.
	 * @return
	 */
	String getIpAddress();
	/**
	 * Sets the ip adress which is associated with this session.
	 * @param anIpAddress
	 */
	void setIpAddress(String anIpAddress);
	
	/**
	 * Returns the user agent string submitted by the browser.
	 * @return
	 */
	String getUserAgent();
	/**
	 * Sets the user agent string for this session. Called by APIFilter.
	 * @param anUserAgent
	 */
	void setUserAgent(String anUserAgent);
	
	/**
	 * Called whenever the user performs an explicit logout
	 */
	void cleanupOnLogout();
	
	/**
	 * Returns the id of the currently logged in user.
	 * @return
	 */
	String getCurrentUserId(); 
	/**
	 * Returns the id of the current CMS editor if applicable.
	 * @return
	 */
	String getCurrentEditorId();
}