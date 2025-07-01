# Static key value maps (KVM) for Apigee X
This repository is used to maintain and deploy to the static KVMs in MGM's Apigee X organizations.

# List of static KVMs
- access-control-allow-origin
    - list of origins allowed in CORS header validation
- security-exclusion
    - CORS: list of paths allowed in CORS header Validation.
    - Security: list of paths used to bypass JWT validation at the service level ( basepath level )
    - PathSecurity: list of paths used to bypass JWT validation at the path level ( API level )
- rate-limiting-quotas 
  - list of rate limiting configurations per X_MGM_SOURCE header in incoming request
- policy-opt-in 
  - list of proxies that participate in setting backend URL into header x-mgm-backend-url
- rcx-jwt-swap-config
  - list of okta variables used for calling and obtaining a new JWT token for RCX
- rcx-jwt-swap-credentials 
  - list of okta secrets used for calling and obtaining a new JWT token for RCX

## security_exclusion
For entries in the security_exclusion KVM, remove the first '/', then replace every '/' and '-' with an '_'

Example: /v1/my-path/endpoint/* &rarr; v1_my_path_endpoint_*

# To Add Your Service
1. If you don't already have it, reach out to the [SRE Apigee team](https://mgmdigitalventures.atlassian.net/l/cp/UCwrr7Wx) to gain write access to the repo.
2. Create a new branch off of either the nonprod or prod branch.
3. In that new branch, go to resources/env/\<desired environment>
    - If you want to apply your changes to multiple environments, you must modify the kvms.json file in **each env directory**
4. Edit the file kvms.json value of your desired KVM to include your origin / base path or path name
5. Create a PR to merge into the nonprod/prod branch.
6. A member within SRE will review the PR request, and the PR merge will trigger a deployment to Apigee X.

Here is an example of how an environment would be laid out:

```
[
  {
    "entry": [
      {
        "name": "CORS",
        "value": "identity_authorization_v1, guestservices_ces, recommendation_api_uw_fa_d_new_v2, recommendation_v2, scimexample"
      },
      {
        "name": "Security",
        "value": "identity_authorization_v1, recommendation_api_uw_fa_d_new_v2, recommendation_v2, acrs_response, scimexample, roomcharge"
      },
      {
        "name": "PathSecurity",
        "value": "v1_reservation_folio_basic, v1_reservations_basic"
      }
    ],
    "name": "security-exclusion"
  },
  {
    "entry": [
      {
        "name": "Access-Control-Allow-Origin",
        "value": "https://localhost:2000, https://dev-checkin.mgmresorts.com, https://qa-checkin.mgmresorts.com"
      }
    ],
    "name": "Access-Control-Allow-Origin"
  }
]
```