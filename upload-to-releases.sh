#!/bin/bash

set -e

REPO='DreaminginCodeZH/DouyaCiBuilds'

tag="$(git describe --long --tags 2>/dev/null | sed 's/^V_//;s/\([0-9]*-g\)/r\1/;s/[-_]/./g')"
body="$(git log -n 1)"

# Get old release by tag
echo "Getting old release by tag..." >&2
response="$(curl -v -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" "https://api.github.com/repos/${REPO}/releases/tags/${tag}")"
echo "${response}" >&2
old_release_id="$(echo "${response}" | jq -r '.id')"

if [[ "${old_release_id}" != "null" ]]; then

    # Delete old release
    echo "Deleting old release..." >&2
    response="$(curl -v -X 'DELETE' -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" "https://api.github.com/repos/${REPO}/releases/${old_release_id}")"
    echo "${response}" >&2
fi

# Create release
echo "Creating release..." >&2
response="$(curl -v -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" -H 'Content-Type: application/json' --data "{ \"tag_name\": \"${tag}\", \"name\": \"${tag}\", \"body\": \"${hash}\" }" "https://api.github.com/repos/${REPO}/releases")"
echo "${response}" >&2
upload_url="$(echo "${response}" | jq -r '.upload_url' | sed 's/{?name,label}$//g')"
echo "Upload url: ${upload_url}" >&2

for file in "$@"; do
    # Upload file
    echo "Uploading file: ${file}" >&2
    name="$(basename "${file}")"
    extension="${name##*.}"
    name="${name%.*}-${tag}.${extension}"
    response="$(curl -v -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" -H "Content-Type: $(file -b --mime-type "${file}")" --data-binary "@${file}" "${upload_url}?name=${name}")"
    echo "${response}" >&2
done
