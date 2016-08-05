#!/bin/bash

set -e

repo="$1"
shift
echo "Repo: ${repo}" >&2

version="$1"
shift
echo "Version: ${version}" >&2

tag="v${version}"
echo "Tag: ${tag}" >&2

body="$1"
shift
echo "Body: ${body}" >&2

# Get old release by tag
echo "Getting old release by tag..." >&2
response="$(curl -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" "https://api.github.com/repos/${repo}/releases/tags/${tag}")"
echo "${response}" >&2
old_release_id="$(echo "${response}" | jq -r '.id')"

if [[ "${old_release_id}" != "null" ]]; then

    # Delete old release
    echo "Deleting old release..." >&2
    response="$(curl -X 'DELETE' -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" "https://api.github.com/repos/${repo}/releases/${old_release_id}")"
    echo "${response}" >&2
fi

# Create release
echo "Creating release..." >&2
response="$(curl -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" -H 'Content-Type: application/json' --data "{ \"tag_name\": $(echo -n "${tag}" | jq -s -R -r @json), \"name\": $(echo -n "${tag}" | jq -s -R -r @json), \"body\": $(echo -n "${body}" | jq -s -R -r @json) }" "https://api.github.com/repos/${repo}/releases")"
echo "${response}" >&2
upload_url="$(echo "${response}" | jq -r '.upload_url' | sed 's/{?name,label}$//g')"
echo "Upload url: ${upload_url}" >&2

for file in "$@"; do
    # Upload file
    echo "Uploading file: ${file}" >&2
    name="$(basename "${file}")"
    response="$(curl -H "Authorization: token ${GITHUB_ACCESS_TOKEN}" -H "Content-Type: $(file -b --mime-type "${file}")" --data-binary "@${file}" "${upload_url}?name=$(echo -n "${name}" | jq -s -R -r @uri)")"
    echo "${response}" >&2
done
