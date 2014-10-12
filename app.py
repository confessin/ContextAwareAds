#!/usr/bin/env python
# encoding: utf-8

"""
API for serving ads based on tags.

TODO: dont consider all the synonyms, may be only consider the top one.
TODO: Remove stop words.
TODO: Dont serve the same ad multiple times.
"""

__author__ = 'mohammad.rafi@inmobi.com (Mohammad Rafi)'


import pprint
import json
from flask import Flask
from flask import request
from nltk.corpus import wordnet as wn
from flask import Response
from flask import send_from_directory
from pymongo import MongoClient

app = Flask(__name__)
app.debug = False
global AD_TAG_MAP
global AD_NAME_MAP
global AD_FILE_MAP

####################
#  Temp Variables  #
####################
MONGO_HOST = 'localhost'
MONGO_PORT = 28017
MONGO_CLIENT = 'ads'

AD_TAGS = {
        1: ['learning', 'education'],  # eucation
        2: ['crash', 'bike', 'automobile'],  # insurance
        3: ['disaster', 'donation']  # Donation
        }


#######################################################################
#                               DB API                                #
#######################################################################
class BaseMongoClient(object):
    """Contains functions for handling and merging mongo service"""

    def __init__(self):
        """@todo: to be defined1 """
        self.collection_family = MONGO_CLIENT
        self.client = MongoClient()
        self.DB = self.client[MONGO_CLIENT]
        self.db = self.DB['ads']

    def get_all_ads(self):
        rows = self.db.find()
        return dict((int(i['id']), (i['tags'], i['name'], i['file_loc'])) for i in rows)


#######################################################################
#                          Generic Functions                          #
#######################################################################

def stream_template(template_name, **context):
    app.update_template_context(context)
    t = app.jinja_env.get_template(template_name)
    rv = t.stream(context)
    rv.enable_buffering(50)
    return rv


def get_ad_tags():
    """TODO: Get Ad Tag map from DB and return it
    :returns: @todo

    """
    db = BaseMongoClient()
    return db.get_all_ads()
    #return AD_TAGS


def preprocess_ad_tags():
    """@todo: Docstring for preprocess_ad_tags.
    :returns: @todo

    """
    ad_tags = get_ad_tags()
    ad_tag_map = {}
    ad_name_map = {}
    ad_file_map = {}
    for ad_key, ad_info in ad_tags.items():
        ad_synsets = []
        for at in ad_info[0]:
            ad_synsets += wn.synsets(at)
        ad_tag_map[ad_key] = ad_synsets
        ad_name_map[ad_key] = ad_info[1]
        ad_file_map[ad_key] = ad_info[2]
    return ad_tag_map, ad_name_map, ad_file_map


def calculate_score(ad_tags, app_tags, app_key):
    """@todo: Docstring for calculate_score.

    :tags_1: list
    :tags_2: list
    :returns: score

    """
    score = 0
    cnt = 0
    reasons = []
    for i in ad_tags:
        for j in app_tags:
            if i.name().split('.')[0] == j.name().split('.')[0]:
                similarity = 1.2
                score += similarity
                reasons.append((similarity, i.name(), j.name(), i.definition(), j.definition()))
                cnt += 1
                continue
            similarity = i.wup_similarity(j)
            if similarity > 0.6:
                print similarity, i.name(), j.name()
                print i.definition()
                print j.definition()
                print '-' * 10
                reasons.append((similarity, i.name(), j.name(), i.definition(), j.definition()))
                score += similarity
                cnt += 1
    if cnt == 0:
        return 0, []
    #score = (score * cnt) / len(AD_TAGS[app_key])
    score = (score * cnt) / len(AD_TAG_MAP[app_key])
    return score, reasons
#######################################################################
#                          Service Methods                           #
#######################################################################


@app.route("/")
def hello():
    return "Hello World!"


@app.route("/get_all_ads")
def get_all_ads():
    return "Hello World!"


@app.route("/get_ad_temp", methods=['POST', 'GET'])
def get_ad_temp():
    app_tags = request.args.get('tags', '')
    if not app_tags:
        return "No App tag found."
    rows = range(10)
    return Response(stream_template('2.html', rows=rows))
    #return "Here is the ad."


@app.route("/get_ad", methods=['POST', 'GET'])
def get_ad():
    app_tags = request.args.get('tags', '')
    if not app_tags:
        return "No App tag found."
    req_debug = request.args.get('debug', '')
    app_tag_list = []
    for app_tag in app_tags.split():
        app_synsets = wn.synsets(app_tag)
        app_tag_list += app_synsets
    shortlist_adgroups = []
    reasons = {}
    for ad_key, ad_tags in AD_TAG_MAP.items():
        score, reason = calculate_score(ad_tags, app_tag_list, ad_key)
        if score > 0:
            shortlist_adgroups.append((ad_key, score))
            reasons[AD_NAME_MAP[ad_key]] = reason
    if not shortlist_adgroups:
        return "No Matching Adgroups found."
    shortlist_adgroups = sorted(shortlist_adgroups, key=lambda x: x[1], reverse=True)
    print 'shortlisted adgroups:', ':::'.join(str(i) for i in shortlist_adgroups if i[1] > 0)
    ad_group = shortlist_adgroups[0]
    #pprint.pprint(reasons[ad_group[0]])
    if req_debug:
        return Response(stream_template(str(ad_group[0]) + '.html',
            score=ad_group[1], reasons=reasons,
            adgroups=[(AD_NAME_MAP[i], j) for i,j in shortlist_adgroups]))
    return Response(stream_template(str(ad_group[0]) + '.html',
        score=ad_group[1]))
    #return Response(stream_template('2.html', score=ad_group[1]))
    #return 'shortlisted adgroups: ' + ':::'.join(str(i) for i in shortlist_adgroups if i[1] > 0)


@app.route("/get_ad_json", methods=['POST'])
def get_ad_json():
    app_tags = request.form.get('tags', '')
    if not app_tags:
        return "No App tag found."
    app_tag_list = []
    for app_tag in app_tags.split():
        app_synsets = wn.synsets(app_tag)
        app_tag_list += app_synsets
    shortlist_adgroups = []
    reasons = {}
    for ad_key, ad_tags in AD_TAG_MAP.items():
        score, reason = calculate_score(ad_tags, app_tag_list, ad_key)
        if score > 0:
            shortlist_adgroups.append((ad_key, score))
            reasons[AD_NAME_MAP[ad_key]] = reason
    if not shortlist_adgroups:
        return "No Matching Adgroups found."
    shortlist_adgroups = sorted(shortlist_adgroups, key=lambda x: x[1], reverse=True)
    print 'shortlisted adgroups:', ':::'.join(str(i) for i in shortlist_adgroups if i[1] > 0)
    ad_group = shortlist_adgroups[0]
    #pprint.pprint(reasons[ad_group[0]])
    return json.dumps({
        'file_name': AD_FILE_MAP[ad_group[0]],
        'score': ad_group[1],
        'name': AD_NAME_MAP[ad_group[0]],
        'reason': reasons[AD_NAME_MAP[ad_key]],
        })



@app.route('/media/<path:filename>')
def send_foo(filename):
    return send_from_directory('media/', filename)


if __name__ == "__main__":
    AD_TAG_MAP, AD_NAME_MAP, AD_FILE_MAP = preprocess_ad_tags()
    #app.run(host= '0.0.0.0', port=10000)
    app.run(host= '0.0.0.0')
