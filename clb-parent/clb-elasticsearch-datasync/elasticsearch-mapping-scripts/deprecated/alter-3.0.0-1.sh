curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/user/_mapping -d '{
	"properties": {
		"projects": {
			"type": "long",
			"index": true
		},
		"invited_projects": {
			"type": "long",
			"index": true
		}
	}
}'
