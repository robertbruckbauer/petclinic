const BACKEND_URL =
  window.location.protocol +
  "//" +
  window.location.host.replace("5050", "8080");

export function apiExplorerUrl() {
  return BACKEND_URL + "/api/explorer";
}

export function apiGraphiqlUrl() {
  return BACKEND_URL + "/api/graphiql";
}

export async function loadAllValue(path) {
  return fetch(BACKEND_URL + path, {
    mode: "cors",
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(path + " failed with code " + res.status);
    })
    .then((json) => {
      return json.content.map((item) => {
        delete item.links;
        delete item.content;
        return item;
      });
    });
}

export async function loadOneValue(path) {
  return fetch(BACKEND_URL + path, {
    mode: "cors",
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(path + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function createValue(path, value) {
  return fetch(BACKEND_URL + path, {
    mode: "cors",
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-type": "application/json",
    },
    body: JSON.stringify(value),
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(path + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function updateValue(path, value) {
  return fetch(BACKEND_URL + path, {
    mode: "cors",
    method: "PUT",
    headers: {
      Accept: "application/json",
      "Content-type": "application/json",
    },
    body: JSON.stringify(value),
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(path + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function updatePatch(path, value) {
  return fetch(BACKEND_URL + path, {
    mode: "cors",
    method: "PATCH",
    headers: {
      Accept: "application/json",
      "Content-type": "application/merge-patch+json",
    },
    body: JSON.stringify(value),
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(path + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function removeValue(path) {
  return fetch(BACKEND_URL + path, {
    mode: "cors",
    method: "DELETE",
    headers: {
      Accept: "application/json",
    },
  })
    .then((res) => {
      if (res.ok) return res.json();
      throw Error(path + " failed with code " + res.status);
    })
    .then((json) => {
      delete json.content;
      delete json.links;
      return json;
    });
}

export async function version() {
  const path = "/version";
  return fetch(BACKEND_URL + path, {
    mode: "cors",
    method: "GET",
    headers: {
      Accept: "text/html",
    },
  }).then((res) => {
    if (res.ok) return res;
    throw Error(path + " failed with " + res.status);
  });
}
