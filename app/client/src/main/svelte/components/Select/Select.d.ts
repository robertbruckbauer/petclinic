import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';

export default class Select extends SvelteTypedComponent<SelectProps, SelectEvents, SelectSlots> {
}

declare const _SelectProps: {

  /** 
   * Array of Items.
   */
  allItem?: ({ value: string, text: string } | string)[];

  /** 
   * Disabled state.
   */
  disabled?: boolean;

    /** 
     * Array of Items.
     */
    allItem?: ({value:string, text: string}|string)[];

  /** 
   * Nullable value.
   */
  nullable?: boolean;

  /** 
   * Selected value.
   */
  value?: string;

  /** 
   * Selected value item.
   */
  valueItem?: object;

  /** 
   * Null value.
   */
  valueNull?: object;

  /**
   * Title for tooltip
   */
  title?: string

} & SvelteAllProps;

declare const _SelectEvents: {
  click: any;
  change: any;
};

declare const _SelectSlots: {
};

export declare type SelectProps = typeof _SelectProps;
export declare type SelectEvents = typeof _SelectEvents;
export declare type SelectSlots = typeof _SelectSlots;
export { };
