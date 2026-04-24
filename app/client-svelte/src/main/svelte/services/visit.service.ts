import { Observable, map } from "rxjs";
import type { Visit, VisitItem } from "../types/visit.type";
import { BackendService } from "./backend.service";

export class VisitService extends BackendService {
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

  public createVisit(visit: Visit): Observable<Visit> {
    const path = ["api", "visit"].join("/");
    return this.restApiPost(path, visit);
  }

  public mutateVisit(id: string, patch: Partial<Visit>): Observable<Visit> {
    const path = ["api", "visit", id].join("/");
    return this.restApiPatch(path, patch);
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
