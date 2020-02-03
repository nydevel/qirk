curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/_scripts/user_add_organization -d '{
	"script": {
		"lang": "painless",
		"source": "ctx._source.organizations.add(params);"
 	}
}'

curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/_scripts/user_update_organization_member -d '{
	"script": {
		"lang": "painless",
		"source": "for ( int i = 0; i < ctx._source.organizations.size(); i ++ ) {if ( ctx._source.organizations[ i ].member_id == params.member_id) {ctx._source.organizations[ i ].enabled = params.enabled;} }"
 	}
}'

curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/_scripts/user_remove_organization -d '{
	"script": {
		"lang": "painless",
		"source": "for ( int i = ctx._source.organizations.size() - 1; i >= 0; i -- ) {if ( ctx._source.organizations[ i ].member_id == params.member_id) {ctx._source.organizations.remove(i);} }"
 	}
}'
