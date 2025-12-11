---
title: API Coverage
layout: default
has_children: true
nav_order: 3
---

# API Coverage

## Overview

BigBone does not yet implement all the endpoints that are offered by the [Mastodon API](https://docs.joinmastodon.org/methods/). We use this page to
provide an overview of which endpoints have been implemented and to what extent. This information applies to the latest version of BigBone. 

We use the __Status__ column to provide information about the current implementation status of this endpoint. Values are as follows:

<table style="width:100%;table-layout:fixed;">
  <tr>
    <th style="width:10%;text-align:center;">Status</th>
    <th style="width:90%;text-align:left;">Description</th>
  </tr>
  <tr>
    <td style="width:10%;text-align:center;"><img src="/assets/green16.png"></td>
    <td style="width:90%;text-align:left;">The endpoint is fully implemented according to the Mastodon API documentation.</td>
  </tr>
  <tr>
    <td style="width:10%;text-align:center;"><img src="/assets/orange16.png"></td>
    <td style="width:90%;text-align:left;">The endpoint is partially implemented, meaning that some of the features / fields mentioned in the Mastodon API documentation are missing.</td>
  </tr>
  <tr>
    <td style="width:10%;text-align:center;"><img src="/assets/red16.png"></td>
    <td style="width:90%;text-align:left;">The endpoint is not supported by the library at the moment.</td>
  </tr>
</table>

In the __Comments__ column, we provide more information what exactly is missing in order to get to a fully implemented state.

## Reactive Implementation
BigBone offers reactive endpoints based on RxJava as well. We have not listed the detailed implementation status here, but we are aware that not all endpoints
are offered as rx endpoints yet. It might also happen that they are not complete in terms of parameters or response objects. We will try to clean this up as 
we go along and implement missing functionality. If you think, a specific rx endpoint is critical to have, please open an issue in the
[Issues](https://github.com/pattafeufeu/bigbone/issues) section or even better, provide a pull request.

