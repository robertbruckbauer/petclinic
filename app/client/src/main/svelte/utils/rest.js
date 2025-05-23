const BACKEND_URL =
  window.location.protocol +
  "//" +
  window.location.host.replace("5000", "8080");

export function apiExplorerUrl() {
  return BACKEND_URL + "/api/explorer";
}

export function apiGraphiqlUrl() {
  return BACKEND_URL + "/api/graphiql";
}

export async function loadAllValue(restUrl) {
  return fetch(BACKEND_URL + restUrl, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(restUrl + " failed with code " + res.status);
    })
    .then((json) => {
      return json.content.map((item) => {
        delete item.links;
        delete item.content;
        return item;
      });
    });
}

export async function loadOneValue(restUrl) {
  return fetch(BACKEND_URL + restUrl, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(restUrl + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function createValue(restUrl, value) {
  return fetch(BACKEND_URL + restUrl, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-type": "application/json",
    },
    body: JSON.stringify(value),
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(restUrl + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function updateValue(restUrl, value) {
  return fetch(BACKEND_URL + restUrl, {
    method: "PUT",
    headers: {
      Accept: "application/json",
      "Content-type": "application/json",
    },
    body: JSON.stringify(value),
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(restUrl + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function updatePatch(restUrl, value) {
  return fetch(BACKEND_URL + restUrl, {
    method: "PATCH",
    headers: {
      Accept: "application/json",
      "Content-type": "application/merge-patch+json",
    },
    body: JSON.stringify(value),
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(restUrl + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function updateLink(restUrl, linkUrl) {
  return fetch(BACKEND_URL + restUrl, {
    method: "PUT",
    headers: {
      "Content-type": "text/uri-list",
    },
    body: linkUrl,
  }).then((res) => {
    if (res.ok) return {};
    throw Error(restUrl + " failed with code " + res.status);
  });
}

export async function removeValue(restUrl) {
  return fetch(BACKEND_URL + restUrl, {
    method: "DELETE",
    headers: {
      Accept: "application/json",
    },
  }).then((res) => {
    if (res.ok) return {};
    throw Error(restUrl + " failed with code " + res.status);
  });
}

export async function fetchDoc(adocUrl, accept) {
  return fetch(BACKEND_URL + adocUrl, {
    method: "GET",
    headers: {
      Accept: accept,
    },
  }).then((res) => {
    if (res.ok) return res;
    throw Error(adocUrl + " failed with " + res.status);
  });
}
