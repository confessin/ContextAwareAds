#!/usr/bin/env python
# encoding: utf-8

"""
TEST
"""

__author__ = 'mohammad.rafi@inmobi.com (Mohammad Rafi)'


import nltk
from nltk.corpus import wordnet as wn


AD_TAGS = ['support', 'disaster', 'money']

APP_TAGS = ['flood', 'kashmir']


def get_ad_tags():
    """@todo: Docstring for get_ad_tags.
    :returns: a map containing

    """
    pass



def main():
    """@todo: Docstring for main.
    :returns: @todo

    """
    foo = []
    for ad_tag in AD_TAGS:
        for app_tag in APP_TAGS:
            ad_synsets = wn.synsets(ad_tag)
            app_synsets = wn.synsets(app_tag)
            for i in ad_synsets:
                for j in app_synsets:

                    score = i.wup_similarity(j)
                    if score > 0.5:
                        foo.append((score, ad_tag, app_tag))
                        print score, ad_tag, app_tag
                        print i.definition()
                        print j.definition()
                        print '-'*10


if __name__ == '__main__':
    main()
