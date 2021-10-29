import os

from flask import Flask

app = Flask( __name__ )

@app.route( "/health" )
def health ():
    return {
        "status": "ok"
    }

@app.route( "/" )
def hello ():
    return 'Hello world from ' + os.environ[ 'HOSTNAME' ] + ' !'

if __name__ == "__main__" :
    app.run(host = '0.0.0.0' ,port = '8000' )

# set FLASK_ENV=development

# http://localhost:8000/
# http://localhost:8000/health/