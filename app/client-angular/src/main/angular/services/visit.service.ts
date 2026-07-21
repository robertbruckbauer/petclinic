import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, map } from "rxjs";
import { type Visit, type VisitItem } from "../types/visit.type";
import { BackendService } from "./backend.service";

@Injectable()
export class VisitService extends BackendService {
  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  public loadAllVisit(
    search: Record<string, string> = {}
  ): Observable<Visit[]> {
    const path = ["api", "visit"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadOneVisit(id: string): Observable<Visit> {
    const path = ["api", "visit", id].join("/");
    return this.restApiGet(path);
  }

  public createVisit(value: Visit): Observable<Visit> {
    const path = ["api", "visit"].join("/");
    return this.restApiPost(path, value);
  }

  public mutateVisit(id: string, value: Partial<Visit>): Observable<Visit> {
    const path = ["api", "visit", id].join("/");
    return this.restApiPatch(path, value);
  }

  public removeVisit(id: string): Observable<Visit> {
    const path = ["api", "visit", id].join("/");
    return this.restApiDelete(path);
  }

  public loadAllVisitByText(text: string): Observable<VisitItem[]> {
    const query = `{ allVisitByText(text: ${JSON.stringify(text)}) { id date pet { name owner { name address } } } }`;
    return this.graphqlQuery<{ allVisitByText: any[] }>(query).pipe(
      map((data) =>
        data.allVisitByText.map((v) => ({
          value: v.id,
          text: `${v.pet?.name} of ${v.pet?.owner?.name}, ${v.pet?.owner?.address} at ${v.date}`,
        }))
      )
    );
  }
}
