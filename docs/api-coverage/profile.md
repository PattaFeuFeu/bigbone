---
title: Profile
layout: default
parent: API Coverage
nav_order: 39
---

# Profile

Methods concerning profiles.

<a href="https://docs.joinmastodon.org/methods/preferences/" target="_blank">https://docs.joinmastodon.org/methods/profile/</a>

<table style="width:100%;table-layout:fixed;">
  <tr>
    <th style="width:45%;text-align:left;">Endpoint</th>
    <th style="width:10%;text-align:center;">Status</th>
    <th style="width:45%;text-align:left;">Comments</th>
  </tr>
  <tr>
    <td style="width:45%;text-align:left;"><code>GET /api/v1/profile</code><br>Get current user profile</td>
    <td style="width:10%;text-align:center;"><img src="/assets/green16.png"></td>
    <td style="width:45%;text-align:left;">Fully supported.</td>
  </tr>
  <tr>
    <td style="width:45%;text-align:left;"><code>PATCH /api/v1/profile</code><br>Update current user profile</td>
    <td style="width:10%;text-align:center;"><img src="/assets/red16.png"></td>
    <td style="width:45%;text-align:left;">Not yet supported.</td>
  </tr>
  <tr>
    <td style="width:45%;text-align:left;"><code>DELETE /api/v1/profile/avatar</code><br>Delete profile avatar</td>
    <td style="width:10%;text-align:center;"><img src="/assets/green16.png"></td>
    <td style="width:45%;text-align:left;">Fully supported.</td>
  </tr>
  <tr>
    <td style="width:45%;text-align:left;"><code>DELETE /api/v1/profile/header</code><br>Delete profile header</td>
    <td style="width:10%;text-align:center;"><img src="/assets/green16.png"></td>
    <td style="width:45%;text-align:left;">Fully supported.</td>
  </tr>
</table>